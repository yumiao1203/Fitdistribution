package com.muzi

/**
  * Created by josh on 17-6-14.
  */
import org.apache.spark.rdd.RDD
object FitUniform {
    def main(args: Array[String]): Unit = {

    }
    def fitUniform(rdd:RDD[Double]):(Double,Double) ={
      val params:(Double,Double) = (rdd.min(),rdd.max())
      params
    }
}
