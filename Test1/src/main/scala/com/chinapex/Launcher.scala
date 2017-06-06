package com.chinapex

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import breeze.linalg.{DenseVector => BDV}
import breeze.stats.distributions._
import com.chinapex.KLDiv_KNN._


/**
  * Created by josh on 17-5-26.
  */
object Launcher extends App {
  final val NUM_DATA_POINTS = 10000000
  val numTasks: Int = 128

  val conf = new SparkConf().setAppName("Fit Distribution").setMaster(s"local[$numTasks]")
  val sc = new SparkContext(conf)
  sc.setLogLevel("ERROR")
  //Test data

//  val realData = Gaussian(0.0,1.0).sample(100)
 // val realData = Gamma(2,2).sample(100)
 val spark = SparkSession.builder
   .master("local")
   .appName("Spark CSV Reader")
   .getOrCreate
  val df = spark.read
    .format("csv")
    .option("inferSchema", "true")
    .option("header", "true")
    .load("/home/josh/Downloads/Datasets/Sberbank_Russian_Housing_Market/test.csv")
  df.printSchema()

  val realData = LogNormal(0,1).sample(100)

  val realBDV = BDV(realData.toArray:_*)


  val param0 = FitUniform.fitUniform(realBDV)
  println(param0)
  val UniformData = Uniform(param0._1, param0._2).sample(100)

  val param1 = FitGaussian.fitGauss(realBDV)
  println(param1)
  val GaussData = Gaussian(param1._1,param1._2).sample(100)

  val param2 = FitGamma.fitGamma(realBDV)
  println(param2)
  val GammaData = Gamma(param2._1,param2._2).sample(100)

  val param3 = FitExponential.fitExponential(realBDV)
  println(param3)
  val ExponData = Exponential(param3).sample(100)

//  val param4 = FitBeta.fitBeta(realBDV)
//  print(param4)
  val param5 =FitLognormal.fitLognormal(realBDV)
  println(param5)
  val LognormData = LogNormal(param5._1,param5._2).sample(100)

  println(execute1(realData.toArray,UniformData.toArray, k = 10))
  println(execute1(realData.toArray,GaussData.toArray,k = 10))
  println(execute1(realData.toArray,GammaData.toArray,k = 10))
  println(execute1(realData.toArray,ExponData.toArray,k = 10))
  println(execute1(realData.toArray,LognormData.toArray,k = 10))
}