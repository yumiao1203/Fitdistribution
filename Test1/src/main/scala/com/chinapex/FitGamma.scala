package com.chinapex

import breeze.stats.distributions.Gamma
import breeze.linalg.{DenseVector => BDV}
import breeze.numerics.log
import breeze.stats._

/**
  * Created by josh on 17-5-26.
  */
object FitGamma {
  def main(args: Array[String]) {
    val vec = BDV(0.1, 0.1, 1.0, 0.5, 0.5, 1.2, 0.3)
    fitGamma(vec)
  }
  def fitGamma (vec: BDV[Double]): (Double,Double) = {
    val logVec = log(vec)
//    println(vec.size,mean(logVec),mean(vec))
    val ss = Gamma.SufficientStatistic(vec.size,mean(logVec), mean(vec))
    val parm = Gamma.mle(ss)
    parm
  }
}
