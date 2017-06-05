package com.chinapex
import breeze.linalg.{ DenseVector => BDV}
import breeze.stats._

/**
  * Created by josh on 17-6-5.
  */
object FitPoisson {
  def main(args: Array[String]) {
    val vec = BDV(0.1, 0.0, 1.0, -0.5, 0.5, 1.2, 0.3)
    fitPoisson(vec)
    println(fitPoisson(vec))
  }
  def fitPoisson(vec:BDV[Double]):Double = {
    //mle
    val param = mean(vec)
    param
  }
}
