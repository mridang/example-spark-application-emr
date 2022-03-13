package com.mridang.spark

import org.apache.hadoop.fs.s3a.S3AFileSystem
import org.apache.logging.log4j.scala.Logging
import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession}

object InitSpark {

  def setupContext(sc: SparkContext): SparkContext = {

    sc.hadoopConfiguration.set("fs.s3a.impl", classOf[S3AFileSystem].getName)
    sc.hadoopConfiguration.set("fs.s3.impl", classOf[S3AFileSystem].getName)
    sc.hadoopConfiguration.set("fs.s3n.impl", classOf[S3AFileSystem].getName)
    sc
  }
}

trait InitSpark extends Logging {
  val spark: SparkSession = SparkSession.builder()
    .appName("Spark example")
    .master("local[*]")
    .config("option", "some-value")
    .config("spark.mongodb.input.uri", "mongodb://prod-mongodb-analytics1.us-east-1.nos.to:27017/cart.xxx")
    .config("spark.mongodb.output.uri", "mongodb://prod-mongodb-analytics1.us-east-1.nos.to:27017/cart.xxx")
    .getOrCreate()

  val sparkContext: SparkContext = InitSpark.setupContext(spark.sparkContext)
  val sqlContext: SQLContext = SparkSession.builder().getOrCreate().sqlContext

  def initSession(): Void => SparkSession = {
    _ => spark
  }

  private def init(): Unit = {
    sparkContext.setLogLevel("ERROR")
  }

  init()

  def close(): Unit = {
    spark.close()
  }
}