package com.muzi

/**
  * Created by josh on 17-6-14.
  */
import breeze.numerics.{abs, log}
import breeze.linalg.{max, sum}
import breeze.stats.distributions. Gaussian
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object KLDiv_KNN {
  val numTasks = 1000
  val conf = new SparkConf().setAppName("KL-Divergence").setMaster(s"local[$numTasks]")
  val sc = new SparkContext(conf)
  sc.setLogLevel("ERROR")

  def main(args: Array[String]): Unit = {
    val samples1 = Gaussian(0,1).sample(10000)
    val samples2 = Gaussian(0,1).sample(10000)
    val samp1RDD = sc.parallelize(samples1)
    val samp2RDD = sc.parallelize(samples2)
    val tic = System.nanoTime()
    println(execute(samp1RDD,samp2RDD,10),(System.nanoTime()-tic)/1e9)

  }

  def execute(Rrdd: RDD[Double], Trdd: RDD[Double], k: Int): Double = {

    val numR = Rrdd.count.toInt
    val numT = Trdd.count.toInt
    val R = Rrdd.collect()
    val T = Trdd.collect()
    val d = 1
    val nearestPointsIndex = new Array[Int](k)
    val distanceArray1 = new Array[Double](numT)
    val distanceArray2 = new Array[Double](numT)
    //    val distanceArray1 = Array.fill(numT)(1.0)
    //    val distanceArray2 = Array.fill(numT)(1.0)
    val crossdif = new Array[Double](numR)
    val dif = new Array[Double](numT)

    if(k > numR || k > numT){
      println("k should be less than sample size!")

    }

    for (i <- 0 until numT) {
      for(j <- 0 until numR ) {
        crossdif(j) = abs(T(i) - R(j))
      }

      for(h <- 0 until k){
        val dif1 = new Array[Double](numR)
        crossdif.copyToArray(dif1)

        val minIndex = crossdif.zipWithIndex.minBy(_._1)._2 //找到最小值所对应的index
        nearestPointsIndex(h) = minIndex
        distanceArray1(i) = distanceArray1(i) + crossdif(nearestPointsIndex(h))
        crossdif(minIndex) = max(crossdif)+1
      }
      for(m <- 0 until numT){
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

