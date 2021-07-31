package com.mridang.spark.hudi

import com.mridang.spark.Amazon.BetterS3
import com.mridang.spark.{Amazon, BetterRDDComparisons, SparkSuiteLike, SqlSuiteLike}
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
    with BeforeAndAfterEach {

  override def extraConf: Map[String, String] =
    Map("spark.serializer" -> "org.apache.spark.serializer.KryoSerializer")

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
    if (Amazon.s3.doesBucketExist(bucketName)) {
      Amazon.s3.truncateBucket(bucketName)
    } else {
      Amazon.s3.createBucket(bucketName)
    }
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
