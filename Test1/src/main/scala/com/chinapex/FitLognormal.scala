package com.chinapex
import breeze.linalg.{DenseVector => BDV}
import breeze.numerics.{exp, log}
import breeze.stats.distributions.Gaussian
import breeze.stats._

/**
  * Created by josh on 17-6-5.
  */
object FitLognormal {
  def main(args: Array[String]): Unit = {
    val vec = BDV(0.1, 0.1, 0.9, 0.5, 0.5, 0.4, 0.3, 0.3, 0.5, 0.9)
    fitLognormal(vec)
    println(fitLognormal(vec))
  }
  def fitLognormal(vec:BDV[Double]): (Double,Double) ={
    val vector = log(vec)
    val ss = Gaussian.SufficientStatistic(vector.size, mean(vector), vector.size * variance(vector))
    val param = Gaussian.mle(ss)
    param
  }
}
