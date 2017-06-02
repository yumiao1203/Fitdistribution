package com.chinapex
import breeze.linalg.{DenseVector => BDV}
import breeze.stats.distributions.{Gaussian, LogNormal}
import breeze.stats._

/**
  * Created by josh on 17-5-27.
  */
object FitLogNormal {
  def main(args: Array[String]) {
    val vec = BDV(0.1, 0.1, 1.0, 0.5, 0.5, 1.2, 0.3)
    fitLogNormal(vec)
  }
  def fitLogNormal (vec: BDV[Double]): Unit = {
    val ss = Gaussian.SufficientStatistic(vec.size, mean(vec), vec.size * variance(vec))
    val parm = LogNormal.mle(ss)
    println(parm)
    LogNormal(0,1).sample(100)
  }
}
