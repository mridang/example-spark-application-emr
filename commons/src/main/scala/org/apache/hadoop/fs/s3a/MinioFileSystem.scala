package org.apache.hadoop.fs.s3a

import com.amazonaws.services.s3.S3ClientOptions
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.permission.FsPermission
import org.apache.hadoop.fs.{FileSystem, FileUtil, Path}
import org.slf4j.{Logger, LoggerFactory}

import java.net.URI

class MinioFileSystem extends S3AFileSystem {

  final val options = S3ClientOptions.builder().setPathStyleAccess(true).build()
  final val S3A_ACCESS_KEY = "fs.s3a.access.key"
  final val S3A_SECRET_KEY = "fs.s3a.secret.key"
  final val S3_ACCESS_KEY = "fs.s3.access.key"
  final val S3_SECRET_KEY = "fs.s3.secret.key"
  val LOG: Logger = LoggerFactory.getLogger(classOf[S3AFileSystem])

  override def initialize(name: URI, originalConf: Configuration): Unit = {
    if (Option(originalConf.get(Constants.ACCESS_KEY)).isEmpty) {
      originalConf.set(S3A_ACCESS_KEY, "minio_username")
      originalConf.set(S3_ACCESS_KEY, "minio_username")
    }
    if (Option(originalConf.get(Constants.SECRET_KEY)).isEmpty) {
      originalConf.set(S3A_SECRET_KEY, "minio_password")
      originalConf.set(S3_SECRET_KEY, "minio_password")
    }
    try {
      super.initialize(name, originalConf)
    } catch {
      case e: Exception =>
        if (!e.getMessage.contains("Method Not Allowed")) {
          throw e
        }
    }
    super.getAmazonS3Client.setS3ClientOptions(options)
    super.getAmazonS3Client.createBucket(name.getHost)
  }

  override def mkdirs(path: Path, permission: FsPermission): Boolean = {
    if (path.toString.equals("/")) {
      true
    } else {
      super.mkdirs(path, permission)
    }
  }

  override def copyFromLocalFile(delSrc: Boolean, overwrite: Boolean, src: Path, dst: Path): Unit = {
    val conf = getConf
    FileUtil.copy(FileSystem.getLocal(conf), src, this, dst, delSrc, overwrite, conf)
  }
}
