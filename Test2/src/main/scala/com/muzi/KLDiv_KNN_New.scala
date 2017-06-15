package com.muzi

/**
  * Created by josh on 17-6-15.
  */

import breeze.linalg.{max, min}
import breeze.numerics.{abs, pow}
import breeze.stats.distributions.Gaussian

object KLDiv_KNN_New {

  def main(args: Array[String]) {
    val samples1 = Gaussian(0,1).sample(100).sortBy(x=>x).toArray
    val samples2 = Gaussian(0,1).sample(100).sortBy(x=>x).toArray
    val tic = System.nanoTime()
    println(execute(1.1,samples1,10))
    println((System.nanoTime()-tic)/1e9)
  }

  def execute(x:Double, Y:Array[Double], k: Int): Double ={
    val Ynum = Y.length
//    assert(k > Ynum, s"k should be less than sample size!")
    val Ysorted = Y.sortBy(x=>x)

    var m = 1
    var Index1 = 0
    var Index2 = Ynum
    var c = 2
    do{
      if(x < Ysorted(Math.ceil(Ynum/2).toInt)){
        Index2 = Math.ceil(Ynum/2).toInt
      }else {
        Index1 = Math.ceil(Ynum/2).toInt
      }
      m = m + 1
      c = pow(2,m)
    }
    while(Index2 - Index1 > 2*k)
    val startPointIndex = max(Index1 - k,0)
    val endPointIndex = min(Index2 + k, Ynum)
    val disArraySize = endPointIndex-startPointIndex
    var difArray = new Array[Double](disArraySize)
    for(i <- startPointIndex to endPointIndex){
      difArray(i-startPointIndex) = abs(x-Ysorted(i))
    }
    val nearestPointsIndex = new Array[Double](k)
    var distance: Double = 0.0
    for(l <- 0 until k) {
      val minIndex1 = difArray.zipWithIndex.minBy(_._1)._2 //找到最小值所对应的index
      nearestPointsIndex(l) = minIndex1
      distance = distance + difArray(1)
      difArray(minIndex1) = max(difArray)
    }
    distance/k
  }

}
