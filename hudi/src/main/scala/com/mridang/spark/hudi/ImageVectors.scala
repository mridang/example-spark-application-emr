package com.mridang.spark.hudi

trait ImageVectors {

  def getAccountId: String

  def getVectors: Seq[Float]

  def getProductId: String

}
