package com.chinapex
import breeze.linalg.{sum, DenseVector => BDV}
import breeze.stats.distributions.Exponential

/**
  * Created by josh on 17-5-26.
  */
object FitExponential {
  def main(args: Array[String]): Unit = {
    val vec = BDV(0.1, 0.1, 1.0, 0.5, 0.5, 1.2, 0.3, 0.3, 0.5, 1.1)
    fitExponential(vec)
  }
  //mle
  def fitExponential (vec: BDV[Double]): Double = {
    val ss = Exponential.SufficientStatistic(vec.size,sum(vec))
    val parm = Exponential.mle(ss)
    parm
  }
}
