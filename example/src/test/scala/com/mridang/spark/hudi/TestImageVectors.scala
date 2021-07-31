package com.mridang.spark.hudi

object TestImageVectors {

  def apply(vectors: ImageVectors): TestImageVectors = {
    TestImageVectors(vectors.getAccountId, vectors.getProductId, vectors.getVectors)
  }
}

case class TestImageVectors(merchant: String, productId: String, vectors: Seq[Float])
  extends ImageVectors {

  override def getAccountId: String = merchant

  override def getVectors: Seq[Float] = vectors

  override def getProductId: String = productId
}
