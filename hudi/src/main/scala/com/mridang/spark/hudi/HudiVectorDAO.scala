package com.mridang.spark.hudi

import org.apache.hudi.DataSourceWriteOptions._
import org.apache.hudi.config.HoodieWriteConfig.{INSERT_PARALLELISM, TABLE_NAME, UPSERT_PARALLELISM}
import org.apache.hudi.keygen.ComplexKeyGenerator
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SaveMode._
import org.apache.spark.sql.{DataFrame, SQLContext}

//noinspection ScalaCustomHdfsFormat
object HudiVectorDAO {

  final val HUDI: String = "hudi"
  final val s3Path: String = "s3://image-vectors/data/"

  def readHudi()(implicit sql: SQLContext): DataFrame = {
    sql.read
      .format(HUDI)
      .load(s3Path + "/*/*")
  }

  def insert(sqlContext: SQLContext,
             relationsRDD: RDD[_ <: ImageVectors],
             update: Boolean): Unit = {
    save(sqlContext,
         relationsRDD.map(vector => HudiVectors(vector)),
         partitions = 2,
         update = update)
  }

  def save(sqlContext: SQLContext,
           imageVectorRdd: RDD[HudiVectors],
           partitions: Int,
           update: Boolean): Unit = {
    val (saveMode, operation) =
      if (update) {
        (Append, UPSERT_OPERATION_OPT_VAL)
      } else {
        (Overwrite, INSERT_OPERATION_OPT_VAL)
      }

    import sqlContext.implicits._

    sqlContext
      .createDataset(imageVectorRdd)
      .toDF()
      .write
      .format(HUDI)
      // Hudi configs are documented in https://hudi.apache.org/docs/configurations.html
      .option(INSERT_PARALLELISM, partitions.toString)
      .option(UPSERT_PARALLELISM, partitions.toString)
      // ComplexKeyGenerator needed when row has compound record (primary) key consisting of multiple columns
      .option(KEYGENERATOR_CLASS_OPT_KEY, classOf[ComplexKeyGenerator].getName)
      // Partition written data under merchant specific folders
      .option(PARTITIONPATH_FIELD_OPT_KEY, "merchant")
      // Unique key inside partition (merchant not needed because it is already partition key)
      .option(RECORDKEY_FIELD_OPT_KEY, "productId,imageId")
      // Used to define latest version of row if multiple updates occur for same row (identified by partition and record key)
      .option(PRECOMBINE_FIELD_OPT_KEY, "updated")
      .option(OPERATION_OPT_KEY, operation)
      .option(ENABLE_ROW_WRITER_OPT_KEY, "true")
      .option(TABLE_NAME, "imagevectors")
      .mode(saveMode)
      .save(s3Path)
  }
}
