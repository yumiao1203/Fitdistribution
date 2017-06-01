package com.chinapex

import breeze.linalg.{max, min, DenseVector => BDV}

/**
  * Created by josh on 17-5-26.
  */
object FitUniform {
  def main(args: Array[String]) {
    val vec = BDV(0.1, 0.1, 1.0, 0.5, 0.5, 1.2, 0.3)
    fitUniform(vec)
  }
  def fitUniform (vec: BDV[Double]): Unit = {
    val parm = (min(vec),max(vec))
    print(parm)
  }
}
