package com.chinapex

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{Column, SparkSession}
import breeze.linalg.{min, DenseVector => BDV}
import breeze.stats.distributions._
import com.chinapex.KNNKL._
import org.apache.spark.mllib.stat.KernelDensity
import org.apache.spark.rdd.RDD


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
  val realData = Gamma(2,2).sample(100)

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

  val param4 = FitBeta.fitBeta(realBDV)
  print(param4)
//  val BetaData = Beta(param4._1,param4._2).sample(100)


//
//  val pi = math.Pi
//
//  def betaFun (a: Double, b: Double): Double = {
//    //def f: Double => Double = (pow(_,a-1))*(pow(1-(_),b-1))
//    val f = (i:Double) => (pow(i,a-1))*(pow(1-i,b-1))
//    val deltaX = 0.01
//    def xn = Stream.iterate(0.0)(_ + deltaX)
//    def xn1 = xn.takeWhile(_ < 1)
//    def yn1  = xn1.map(f)
//    val B = yn1.max
//    B
//  }
//
//  def gammaFun (a: Double): Double = {
//    val g = (t:Double) => pow(t,a-1)*pow(math.E,-t)
//    val deltaX = 0.01
//    def xn = Stream.iterate(0.0)(_ + deltaX)
//    def xn1 = xn.takeWhile(_ < 10000)
//    def yn1  = xn1.map(g)
//    val G = yn1.max
//    G
//  }
//
//  // One Gaussian distribution
//  val gaussian = (x: Double) => {
//    val u = param1._1
//    val d = param1._2
//    exp(-pow((x-u),2)/(2*d*d))/(d*pow(2*pi,1/2))
//  }
//
//  // Uniform distribution
//  val uniform = (x: Double) => x
//
//  //Cauchy distribution
//  val cauchy = (x: Double, a: Double, b: Double) =>
//    1/pi*b/((x-a)*(x-a)-b*b)
//
//  //t distribution
//  val t = (x: Double, v: Double) => {
//    val B = betaFun(0.5,0.5*v)
//    pow((1+x*x/v),(-(v+1)/2)) / (B*sqrt(v))
//  }
//
//  // F distribution
//  val F =(x: Double, v1: Double, v2: Double) => {
//    gammaFun((v1+v2)/2)*pow((v1/v2),v1/2)*pow(x,(v1/2-1))/
//      gammaFun(v1/2)*gammaFun(v2/2)*pow((1+pow(v1,x)/v2),(v1+v2)/2)
//  }
//
//  // Chi-Square distribution x>=0
//  val chiSquare = (x: Double, v: Double) => {
//    pow(math.E, -x/2)*pow(x,v/2-1)/
//      pow(2,v/2)*gammaFun(v/2)
//  }
//
//  // Exponential Distribution
//  val exponential = (x: Double) => {
//    val lamda = param3
//      lamda*exp(-lamda*x)
//  }
//
//  // Weibull Distribution u=0,a=1 standard Weibull distribution
//  val weibull = (x: Double, u:Double, a: Double, b: Double) => {
//    b/a*pow((x-u)/a,b-1)*pow(math.E,-pow((x-u)/a,b))
//  }
//
//  // Log normal distribution
//  val logNormal = (x: Double) => {
//    val lx = log(x)
//    pi/x*exp(-lx*lx)
//  }
//
//  // Gamma distribution
//  val gamma = (x: Double) => {
//    val alpha = param2._1
//    val lamda = param2._2
//    (pow(x,alpha-1)*pow(lamda,alpha)*exp(-lamda*x))/
//      (gammaFun(alpha))
//  }
//
//
////  // Log Gamma distribution
////  val logGamma = (x: Double, alpha: Int, beta: Int) =>
////    exp(beta*x)*exp(-exp(x)/alpha)/
////      (pow(alpha, beta)*fact(beta-1))
//
//  // Simple computation of m! (for beta)
//  def fact(m: Int): Int = if(m < 2) 1 else m*fact(m-1)
//  // Normalization factor for Beta
//  val cBeta = (n: Int, m: Int) => {
//    val f = if(n < 2) 1 else fact(n-1)
//    val g = if(m < 2) 1 else fact(m-1)
//    f*g/fact(n+m -1).toDouble
//  }
//
//  // Beta distribution
//  val beta = (x: Double, alpha: Int, beta: Int) =>
//    pow(x, alpha-1)*pow(x, beta-1)/cBeta(alpha, beta)
//

  println(execute1(realData.toArray,GaussData.toArray,10))
  println(execute1(realData.toArray,GammaData.toArray,10))
  println(execute1(realData.toArray,ExponData.toArray,10))
  println(execute1(realData.toArray,UniformData.toArray,10))



}