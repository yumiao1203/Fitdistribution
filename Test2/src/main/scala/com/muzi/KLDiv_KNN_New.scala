package com.muzi

/**
  * Created by josh on 17-6-15.
  */

import breeze.linalg.{max, min, sum}
import breeze.numerics.{abs, ceil, log, pow}
import breeze.stats.distributions.{Exponential, Gamma, Gaussian}

object KLDiv_KNN_New {

  def main(args: Array[String]) {
    val samples1 = Gaussian(0,1).sample(100000).sortBy(x => x).toArray //真实的
    val samples2 = Gaussian(0,1).sample(100000).sortBy(x => x).toArray //理论的
    val samples3 = Gamma(2,2).sample(100000).sortBy(x => x).toArray
    val samples4 = Exponential(2).sample(100000).sortBy(x => x).toArray
    val numSamp1 = samples1.length
    val numSamp2 = samples2.length
    val d = 1
    val tic = System.nanoTime()
    val distanceArray1 = new Array[Double](100000)
    val distanceArray2 = new Array[Double](100000)
    for(i <- 0 until numSamp1){
      distanceArray1(i) = log(execute(samples1(i), samples4, 10))
      distanceArray2(i) = log(execute(samples1(i), samples1, 10))
    }
    val KL = log(numSamp2/(numSamp1-1))+ d* sum(distanceArray1)/numSamp1 - d* sum(distanceArray2)/numSamp1

    println(s"KL:"+KL)
    println((System.nanoTime()-tic)/1e9)
  }

  def execute(x: Double, Ysorted: Array[Double], k: Int): Double ={
    val Ynum = Ysorted.length
//    assert(k > Ynum, s"k should be less than sample size!")
    var m = 1
    var Index1 = 0
    var Index2 = Ynum-1
    var c = 2
    do{
      if(x <= Ysorted(ceil(Ynum/c)+ Index1)){
        Index2 = ceil(Ynum/c)+ Index1
      }else {
        Index1 = ceil(Ynum/c)+ Index1
      }
      m = m + 1
      c = pow(2,m)
    } while(Index2 - Index1 > 2*k)

    val startPointIndex = max(Index1 - k, 0)
    val endPointIndex = min(Index2 + k, Ynum -1)
    val difArraySize = endPointIndex-startPointIndex
    val difArray = new Array[Double](difArraySize)
    for(i <- startPointIndex until endPointIndex){
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
