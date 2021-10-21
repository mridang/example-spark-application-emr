package com.mridang.spark.hudi

import com.dimafeng.testcontainers.{ForAllTestContainer, MinioContainer}
import com.mridang.spark.{BetterRDDComparisons, SparkSuiteLike, SqlSuiteLike}
import org.apache.hadoop.fs.Path
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}

import scala.util.Random.{nextFloat, nextInt, nextString}

@RunWith(classOf[JUnitRunner])
class HudiVectorDAOTest
  extends FunSuite
    with SparkSuiteLike
    with SqlSuiteLike
    with BetterRDDComparisons
    with BeforeAndAfterEach
    with ForAllTestContainer {

  override val container: MinioContainer = MinioContainer()

  override def extraConf: Map[String, String] =
    Map(
      "spark.serializer" -> "org.apache.spark.serializer.KryoSerializer",
      "spark.hadoop.fs.s3.endpoint" -> s"http://s3.dev.nos.to:${container.mappedPort(9000)}",
      "spark.hadoop.fs.s3.impl" -> "org.apache.hadoop.fs.s3a.MinioFileSystem",
      "spark.hadoop.fs.s3a.endpoint" -> s"http://s3.dev.nos.to:${container.mappedPort(9000)}",
      "spark.hadoop.fs.s3a.impl" -> "org.apache.hadoop.fs.s3a.MinioFileSystem",
      "spark.serializer" -> "org.apache.spark.serializer.KryoSerializer"
    )

  override def conf: SparkConf = new SparkConf().setAll(super.conf.getAll)
    .set("spark.hadoop.fs.s3.endpoint", "http://s3.dev.nos.to:" + container.mappedPort(9000))
    .set("spark.hadoop.fs.s3.impl", "org.apache.hadoop.fs.s3a.MinioFileSystem")
    .set("spark.hadoop.fs.s3a.endpoint", "http://s3.dev.nos.to:" + container.mappedPort(9000))
    .set("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.MinioFileSystem")
    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

  /**
    * Generates a random vector record with 1024 embeddings for Cassandra
    *
    * @param merchant the merchant for whom to generate the vectors
    * @return the vector record
    */
  private def vector(merchant: String): ImageVectors = {
    val vectors: Seq[Float] = Seq.fill(256) {
      nextFloat()
    }
    new TestImageVectors(merchant, nextString(10), vectors)
  }

  /**
    * Generates a random chunk of vector records for the specified merchant
    *
    * @param merchant the merchant for whom to generate the vectors
    * @return the list of generated vector records
    */
  private def vectors(merchant: String): Seq[ImageVectors] = {
    Seq.fill(nextInt(500))(vector(merchant))
  }

  private def resetBucket(bucketName: String): Unit = {
    new Path(s"s3://$bucketName/")
      .getFileSystem(spark.sparkContext.hadoopConfiguration)
      .delete(new Path(s"s3://$bucketName/"), true)
  }

  test("that saving and querying a single merchant works as expected") {
    resetBucket("nosto-image-vectors")

    val rdd: RDD[ImageVectors] = {
      sc.parallelize(
        List(vectors("beer"), vectors("cava"), vectors("vino")).flatten,
        1)
    }.map((vector: ImageVectors) => HudiVectors(vector))

    HudiVectorDAO.insert(spark, rdd, update = false)

    val resultRDD: RDD[HudiVectors] = {
      new HudiVectorRDD(spark, Seq("beer")).of()
    }

    val expectedRDD: RDD[ImageVectors] = rdd
      .filter((vector: ImageVectors) => vector.getAccountId == "beer")
      .map((row: ImageVectors) => (row.getAccountId, row))
      .groupByKey(numPartitions = 1)
      .repartition(numPartitions = 1)
      .flatMap(_._2)

    assertRDDEquals[ImageVectors, TestImageVectors](
      expectedRDD,
      resultRDD,
      (record: ImageVectors) => TestImageVectors(record))
  }

  ignore("that saving and querying multiple merchants works as expected") {
    resetBucket("nosto-image-vectors")

    val rdd: RDD[ImageVectors] = {
      sc.parallelize(
        List(vectors("beer"), vectors("cava"), vectors("vino")).flatten,
        1)
    }

    HudiVectorDAO.insert(spark, rdd, update = false)

    val resultRDD: RDD[HudiVectors] = {
      HudiVectorRDD(sc, Seq("beer", "cava", "vino")).of()
    }

    val expectedRDD: RDD[ImageVectors] = {
      rdd
        .map((row: ImageVectors) => (row.getAccountId, row))
        .groupByKey(numPartitions = 3)
        .repartition(numPartitions = 3)
        .flatMap(_._2)
    }

    assertRDDEquals[ImageVectors, TestImageVectors](
      expectedRDD,
      resultRDD,
      (record: ImageVectors) => TestImageVectors(record))
  }
}
