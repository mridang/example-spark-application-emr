package com.mridang.spark.hudi

import java.util.Date

object HudiVectors {

  def apply(vector: ImageVectors): HudiVectors = {
    new HudiVectors(vector.getAccountId,
      vector.getProductId,
      vector.getVectors.map(score => score.floatValue()))
  }
}

case class HudiVectors(accountId: String,
                       productId: String,
                       vectors: Seq[Float],
                       updated: Long = new Date().getTime)
  extends ImageVectors {

  override def getAccountId: String = accountId

  override def getProductId: String = productId

  override def getVectors: Seq[Float] = vectors
}
