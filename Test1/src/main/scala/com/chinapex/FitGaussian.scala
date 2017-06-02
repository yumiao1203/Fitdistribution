package com.chinapex

import breeze.stats.distributions.Gaussian
import breeze.linalg.{DenseVector => BDV}
import breeze.stats._
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
/**
  * Created by josh on 17-5-26.
  */
object FitGaussian {
  def main(args: Array[String]) {
    val vec = BDV(0.1, 0.0, 1.0, -0.5, 0.5, 1.2, 0.3)
    fitGauss(vec)
  }
  def fitGauss (vec: BDV[Double]): (Double,Double) = {
    val ss = Gaussian.SufficientStatistic(vec.size, mean(vec), vec.size * variance(vec))
    val parm = Gaussian.mle(ss)
    parm
  }


}
