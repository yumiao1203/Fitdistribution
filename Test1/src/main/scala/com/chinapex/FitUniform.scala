package com.chinapex
import breeze.linalg.{max, min, sum, DenseVector => BDV}
/**
  * Created by josh on 17-6-2.
  */
object FitUniform {
  def main(args: Array[String]): Unit = {
    val vec = BDV(0.1, 0.1, 1.0, 0.5, 0.5, 1.2, 0.3, 0.3, 0.5, 1.1)
    fitUniform(vec)
  }

  def fitUniform(vec:BDV[Double]):(Double,Double) ={
    val params:(Double,Double) = (min(vec),max(vec))
    params
  }
}
