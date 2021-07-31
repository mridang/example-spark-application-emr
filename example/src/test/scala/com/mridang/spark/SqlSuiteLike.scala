/*******************************************************************************
  * Copyright (c) 2018 Nosto Solutions Ltd All Rights Reserved.
  * <p>
  * This software is the confidential and proprietary information of
  * Nosto Solutions Ltd ("Confidential Information"). You shall not
  * disclose such Confidential Information and shall use it only in
  * accordance with the terms of the agreement you entered into with
  * Nosto Solutions Ltd.
 ******************************************************************************/
package com.mridang.spark

import org.apache.spark.sql.{SQLContext, SparkSession}

trait SqlSuiteLike { self: SparkSuiteLike =>
  implicit lazy val spark: SQLContext = SparkSession
    .builder()
    .config(sc.getConf)
    .getOrCreate()
    .sqlContext
}
