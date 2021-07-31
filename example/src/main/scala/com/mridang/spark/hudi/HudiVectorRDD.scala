/*******************************************************************************
  * Copyright (c) 2018 Nosto Solutions Ltd All Rights Reserved.
  * <p>
  * This software is the confidential and proprietary information of
  * Nosto Solutions Ltd ("Confidential Information"). You shall not
  * disclose such Confidential Information and shall use it only in
  * accordance with the terms of the agreement you entered into with
  * Nosto Solutions Ltd.
 ******************************************************************************/
package com.mridang.spark.hudi

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SQLContext, SparkSession}

import scala.annotation.meta.param

object HudiVectorRDD {

  def apply(sparkContext: SparkContext): HudiVectorRDD = {
    new HudiVectorRDD(SparkSession.builder.getOrCreate().sqlContext, Seq.empty)
  }

  def apply(sqlContext: SQLContext): HudiVectorRDD = {
    new HudiVectorRDD(sqlContext, Seq.empty)
  }

  def apply(sparkContext: SparkContext, accountIds: Seq[String]): HudiVectorRDD = {
    new HudiVectorRDD(SparkSession.builder.getOrCreate().sqlContext, accountIds)
  }
}

class HudiVectorRDD(@(transient @param) sqlContext: SQLContext,
                    accountIds: Seq[String])
    extends Serializable {

  def of(): RDD[HudiVectors] = {
    val xxx: Array[String] = accountIds.toArray
    import sqlContext.implicits._

    HudiVectorDAO
      .readHudi()(sqlContext)
      .as[HudiVectors]
      .filter { row =>
        {
          //noinspection SimplifyBooleanMatch
          xxx.nonEmpty match {
            case true => xxx.contains(row.accountId)
            case _ => true
          }
        }
      }
      .rdd
  }
}
