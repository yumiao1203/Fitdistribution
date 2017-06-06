package com.chinapex
import breeze.stats.distributions.Gaussian
import breeze.linalg.{DenseVector => BDV}
import breeze.stats._



object FitGaussian {
  def main(args: Array[String]): Unit = {
    val vec = BDV(0.1, 0.1, 1.0, 0.5, 0.5, 1.2, 0.3, 0.3, 0.5, 1.1)
    fitGauss(vec)
  }
  //mle
  def fitGauss (vec: BDV[Double]): (Double,Double) = {
    val ss = Gaussian.SufficientStatistic(vec.size, mean(vec), vec.size * variance(vec))
    val param = Gaussian.mle(ss)
    param
  }
}