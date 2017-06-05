package com.chinapex
import breeze.numerics.{abs, log, pow}
import breeze.linalg.{max, min, sum, DenseVector => BDV}
import breeze.stats.distributions.{Gamma, Gaussian}

/**
  * Created by josh on 17-5-31.
  */

object KLDiv_KNN {
  def main(args: Array[String]): Unit = {
    val samples1 = Gaussian(0,1).sample(100)
    val samples2 = Gamma(1,2).sample(100)
    val R = samples1.toArray
    val T = samples2.toArray
//    val R = Array(1.1, 0.4, 3.1, 0.8, 0.1, 1.4, 1.7, 2.3, 9) //generate samples
//    val T = Array(0.8, 0.9, 0.7, 0.3, 0.3, 0.9, 1.4, 8) //real data
    println(execute1(R,T,10))
  }

  def execute1(R: Array[Double], T: Array[Double], k: Int): Double = {

    val numR = R.length
    val numT = T.length
    val d = 1
    val nearestPointsIndex = new Array[Int](k)
    val distanceArray1 = new Array[Double](numT)
    val distanceArray2 = new Array[Double](numT)
    val crossdif = new Array[Double](R.length)
    val dif = new Array[Double](T.length)

    if(k > R.length || k > T.length){
      println("k should be less than sample size!")

    }

    for (i <- 0 until T.length) {
      for(j <- 0 until R.length ) {
        crossdif(j) = abs(T(i) - R(j))
      }

      for(h <- 0 until k){
        val dif1 = new Array[Double](R.length)
        crossdif.copyToArray(dif1)

        val minIndex = crossdif.zipWithIndex.minBy(_._1)._2 //找到最小值所对应的index
        nearestPointsIndex(h) = minIndex
        distanceArray1(i) = distanceArray1(i) + crossdif(nearestPointsIndex(h))
        crossdif(minIndex) = max(crossdif)+1
      }
      for(m <- 0 until T.length){
        dif(m) = abs(T(m) - T(i))
        dif(i) = max(dif)+1
      }
        for(l <- 0 until k) {
          val minIndex1 = dif.zipWithIndex.minBy(_._1)._2 //找到最小值所对应的index
          nearestPointsIndex(l) = minIndex1
          distanceArray2(i) = distanceArray2(i) + dif(nearestPointsIndex(l))
          dif(minIndex1) = max(dif)
        }

        distanceArray1(i) = log(distanceArray1(i)/k)
        distanceArray2(i) = log(distanceArray2(i)/k)
    }
    val KL = log(numR/(numT-1))+ d* sum(distanceArray1)/numT - d* sum(distanceArray2)/numT
    KL
  }
}
