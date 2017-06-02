package com.chinapex

import breeze.numerics.abs

/**
  * Created by josh on 17-5-31.
  */
object computeMinDif {
  def main(args: Array[String]): Unit = {
    val array = Array(0.1, 0.1, 0.4, 0.5, 0.6)
    computeMinDif(array)
    for(i <- 0 until 5) {
      println(array.count({x:Double => x == array(i)}))
    }
  }

  def computeMinDif(array:Array[Double]): Unit={
    val n = array.length
    val dif = new Array[Double](n*n)
    for(i <- 0 until n){
      for(j <- 0 until n){
         dif(i*n+j) = abs(array(i)-array(j))
      }
    }
    dif.foreach(println)
  }
}
