package com.muzi

import org.apache.spark.rdd.RDD

/**
  * Created by josh on 17-6-14.
  */
object FitGaussian {
  def main(args: Array[String]): Unit = {

  }
  def fitGauss(rdd:RDD[Double]):(Double,Double) = {
  val params:(Double,Double) = (rdd.mean,rdd.sampleVariance)
    params
  }
}
