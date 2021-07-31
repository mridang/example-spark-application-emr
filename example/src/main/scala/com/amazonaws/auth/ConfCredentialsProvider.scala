package com.amazonaws.auth

import com.typesafe.config.Config

case class ConfCredentialsProvider(conf: Config) extends AWSCredentialsProvider {

  val credentials: AWSCredentials = {
    val username: String = conf.getString("aws.s3.access-key")
    val password: String = conf.getString("aws.s3.secret-key")
    new BasicAWSCredentials(username, password)
  }

  override def getCredentials: AWSCredentials = {
    credentials
  }

  override def refresh(): Unit = {
    //
  }
}
