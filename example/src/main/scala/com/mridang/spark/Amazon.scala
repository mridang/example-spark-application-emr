package com.mridang.spark

import com.amazonaws.auth.ConfCredentialsProvider
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3Client, S3ClientOptions}
import com.mridang.spark.Conf.GenericConfig

import scala.collection.JavaConverters._

object Amazon {

  implicit class BetterS3(s3: AmazonS3) {
    def truncateBucket(bucketName: String): Unit = {
      val objectListing = s3.listObjects(bucketName)

      objectListing.getObjectSummaries.asScala
        .foreach { s3ObjectSummary =>
          s3.deleteObject(bucketName, s3ObjectSummary.getKey)
        }
    }
  }

  lazy val s3: AmazonS3 = {
    val config: S3ClientOptions = new S3ClientOptions()
    config.setPathStyleAccess(true)

    val builder = new AmazonS3Client(ConfCredentialsProvider(Conf.conf))
    builder.setRegion(Region.getRegion(Regions.US_EAST_1))
    Conf.conf
      .optionally[String]("aws.s3.endpoint-url")
      .foreach(builder.setEndpoint)
    builder.setS3ClientOptions(config)

    // Register shutdown hook to close S3 client in JVM shutdown
    //sys.addShutdownHook(s3Client.shutdown())
    builder
  }
}
