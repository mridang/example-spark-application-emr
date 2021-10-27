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
    .config("spark.mongodb.input.uri", "mongodb://localhost:37017/test.xxx")
    .config("spark.mongodb.output.uri", "mongodb://localhost:37017/test.xxx")
    .getOrCreate()

  //sparkContext.getConf.getAll.foreach(xxx => println(xxx._1 + ": " + xxx._2))

  val sparkContext: SparkContext = InitSpark.setupContext(spark.sparkContext)
  val sqlContext: SQLContext = new SQLContext(sparkContext)

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