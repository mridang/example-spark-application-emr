package com.mridang.spark

import org.apache.hadoop.fs.s3a.S3AFileSystem
import org.apache.logging.log4j.scala.Logging
import org.apache.spark.SparkContext
import org.apache.spark.sql.{DataFrameReader, SQLContext, SparkSession}

object InitSpark {

  def setupContext(sc: SparkContext): SparkContext = {
    sc.hadoopConfiguration.set("mapreduce.input.fileinputformat.split.minsize", Int.MaxValue.toString)

    sc.hadoopConfiguration.setInt("fs.s3a.multipart.size", 104857600)
    sc.hadoopConfiguration.setInt("fs.s3a.threads.max", 256)
    sc.hadoopConfiguration.setInt("fs.s3a.block.size", 32 * 1024 * 1024)
    sc.hadoopConfiguration.set("fs.s3a.impl", classOf[S3AFileSystem].getName)
    sc.hadoopConfiguration.set("fs.s3a.endpoint", Conf.conf.getString("aws.s3.endpoint-url"))
    sc.hadoopConfiguration.set("fs.s3a.access.key", Conf.conf.getString("aws.s3.access-key"))
    sc.hadoopConfiguration.set("fs.s3a.secret.key", Conf.conf.getString("aws.s3.secret-key"))
    sc.hadoopConfiguration.set("fs.s3a.path.style.access", "true")

    sc.hadoopConfiguration.set("fs.s3.impl", classOf[S3AFileSystem].getName)
    sc.hadoopConfiguration.set("fs.s3.endpoint", Conf.conf.getString("aws.s3.endpoint-url"))
    sc.hadoopConfiguration.set("fs.s3.access.key", Conf.conf.getString("aws.s3.access-key"))
    sc.hadoopConfiguration.set("fs.s3.secret.key", Conf.conf.getString("aws.s3.secret-key"))
    sc.hadoopConfiguration.set("fs.s3.path.style.access", "true")

    sc.hadoopConfiguration.set("fs.s3n.impl", classOf[S3AFileSystem].getName)
    sc.hadoopConfiguration.set("fs.s3n.endpoint", Conf.conf.getString("aws.s3.endpoint-url"))
    sc.hadoopConfiguration.set("fs.s3n.access.key", Conf.conf.getString("aws.s3.access-key"))
    sc.hadoopConfiguration.set("fs.s3n.secret.key", Conf.conf.getString("aws.s3.secret-key"))
    sc.hadoopConfiguration.set("fs.s3n.path.style.access", "true")
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

  def reader: DataFrameReader = spark.read
    .option("header", true)
    .option("inferSchema", true)
    .option("mode", "DROPMALFORMED")

  def readerWithoutHeader: DataFrameReader = spark.read
    .option("header", true)
    .option("inferSchema", true)
    .option("mode", "DROPMALFORMED")

  private def init: Unit = {
    sparkContext.setLogLevel("ERROR")
  }

  init

  def close: Unit = {
    spark.close()
  }
}
