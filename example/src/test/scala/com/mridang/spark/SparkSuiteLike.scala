/** *****************************************************************************
  * Copyright (c) 2018 Nosto Solutions Ltd All Rights Reserved.
  * <p>
  * This software is the confidential and proprietary information of
  * Nosto Solutions Ltd ("Confidential Information"). You shall not
  * disclose such Confidential Information and shall use it only in
  * accordance with the terms of the agreement you entered into with
  * Nosto Solutions Ltd.
  * *****************************************************************************/
package com.mridang.spark

import com.holdenkarau.spark.testing.SharedSparkContext
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.Suite

trait SparkSuiteLike extends SharedSparkContext { self: Suite =>

  abstract override def setup(sc: SparkContext): Unit = {
    super.setup(sc)
    InitSpark.setupContext(sc)
  }

  abstract override def conf: SparkConf = {
    val conf = super.conf
    conf.set("spark.streaming.clock", "org.apache.spark.streaming.util.TestManualClock")
    extraConf.foreach {
      case (k, v) => conf.set(k, v)
    }
    conf
  }

  def extraConf: Map[String, String] = Map.empty

  override protected implicit def reuseContextIfPossible: Boolean =
    extraConf.isEmpty
}
