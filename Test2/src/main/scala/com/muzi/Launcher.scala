package com.muzi

import breeze.stats.distributions.{Gaussian, LogNormal, Uniform}
import org.apache.spark.{SparkConf, SparkContext}
import breeze.linalg.{DenseVector => BDV}


/**
  * Created by josh on 17-6-14.
  */
object Launcher extends App {
  final val NUM_DATA_POINTS = 10000000
  val numTasks: Int = 128
  val numSamples: Int = 1000

  val conf = new SparkConf().setAppName("Fit Distribution").setMaster(s"local[$numTasks]")
  val sc = new SparkContext(conf)
  sc.setLogLevel("ERROR")


  val realData = LogNormal(0, 1).sample(numSamples)
  val realRDD = sc.parallelize(realData)
  println(realRDD.count())
  val realArray = realRDD.collect()


  val realBDV = BDV(realData.toArray: _*)


  val param0 = FitUniform.fitUniform(realRDD)
  println(param0)
  val UniformData = Uniform(param0._1, param0._2).sample(numSamples)

  val param1 = FitGaussian.fitGauss(realRDD)
  println(param1)
  val GaussData = Gaussian(param1._1, param1._2).sample(numSamples)

//  val param2 = FitGamma.fitGamma(realBDV)
//  println(param2)
//  val GammaData = Gamma(param2._1, param2._2).sample(numSamples)
//
//  val param3 = FitExponential.fitExponential(realBDV)
//  println(param3)
//  val ExponData = Exponential(param3).sample(numSamples)
//
//  //  val param4 = FitBeta.fitBeta(realBDV)
//  //  print(param4)
//  val param5 = FitLognormal.fitLognormal(realBDV)
//  println(param5)
//  val LognormData = LogNormal(param5._1, param5._2).sample(numSamples)

//  println(execute1(realData.toArray, UniformData.toArray, k = 10))
//  println(execute1(realData.toArray, GaussData.toArray, k = 10))
//  println(execute1(realData.toArray, GammaData.toArray, k = 10))
//  println(execute1(realData.toArray, ExponData.toArray, k = 10))
//  println(execute1(realData.toArray, LognormData.toArray, k = 10))
}


