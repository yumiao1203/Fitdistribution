
package com.chinapex

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{Column, SparkSession}
import breeze.linalg.{min, DenseVector => BDV}
import breeze.numerics._
import com.chinapex.FitGamma._
import com.chinapex.FitGaussian._
import com.chinapex.FitExponential._
import com.chinapex.FitLogNormal._
import org.sameersingh.scalaplot.Implicits._
import breeze.stats._
import breeze.stats.distributions.Gaussian
import org.apache.spark.h2o.Dataset
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.types.DoubleType
import com.chinapex.KNNKL._


object Launcher extends App {
  final val NUM_DATA_POINTS = 10000000
  val numTasks: Int = 128

  val conf = new SparkConf().setAppName("Fit Distribution").setMaster(s"local[$numTasks]")
  val sc = new SparkContext(conf)
  sc.setLogLevel("ERROR")
  //Test data

  val realData = Gaussian(0.0,1.0).sample(100)
  realData
  val realBDV = BDV(realData.toArray:_*)


  val data = BDV(1.1, 2.1, 2.1, 2.1, 0.1, 0.63, 0.69, 0.69, 3.1, 3.1)

  //val realData = BDV(0.1, 0.1, 0.1, 0.1, 0.2, 0.2, 0.2, 0.4, 0.9, 1.3)
  val meanHat = meanAndVariance(realBDV).mean
  val varianceHat = meanAndVariance(realBDV).variance
  val Rdata = Gaussian(meanHat,varianceHat).sample(100)

  val param1 = FitGaussian.fitGauss(data)
  println(param1)
  val gaussData = Gaussian(param1._1,param1._2).sample(10)


  val param2 = FitGamma.fitGamma(data)
  println(param2)

  val param3 = FitExponential.fitExponential(data)
  println(param3)

  val pi = math.Pi

  def betaFun (a: Double, b: Double): Double = {
    //def f: Double => Double = (pow(_,a-1))*(pow(1-(_),b-1))
    val f = (i:Double) => (pow(i,a-1))*(pow(1-i,b-1))
    val deltaX = 0.01
    def xn = Stream.iterate(0.0)(_ + deltaX)
    def xn1 = xn.takeWhile(_ < 1)
    def yn1  = xn1.map(f)
    val B = yn1.max
    B
  }

  def gammaFun (a: Double): Double = {
    val g = (t:Double) => pow(t,a-1)*pow(math.E,-t)
    val deltaX = 0.01
    def xn = Stream.iterate(0.0)(_ + deltaX)
    def xn1 = xn.takeWhile(_ < 10000)
    def yn1  = xn1.map(g)
    val G = yn1.max
    G
  }

  // One Gaussian distribution
  val gaussian = (x: Double) => {
    val u = param1._1
    val d = param1._2
    exp(-pow((x-u),2)/(2*d*d))/(d*pow(2*pi,1/2))
  }

  // Uniform distribution
  val uniform = (x: Double) => x

  //Cauchy distribution
  val cauchy = (x: Double, a: Double, b: Double) =>
    1/pi*b/((x-a)*(x-a)-b*b)

  //t distribution
  val t = (x: Double, v: Double) => {
    val B = betaFun(0.5,0.5*v)
    pow((1+x*x/v),(-(v+1)/2)) / (B*sqrt(v))
  }

  // F distribution
  val F =(x: Double, v1: Double, v2: Double) => {
    gammaFun((v1+v2)/2)*pow((v1/v2),v1/2)*pow(x,(v1/2-1))/
      gammaFun(v1/2)*gammaFun(v2/2)*pow((1+pow(v1,x)/v2),(v1+v2)/2)
  }

  // Chi-Square distribution x>=0
  val chiSquare = (x: Double, v: Double) => {
    pow(math.E, -x/2)*pow(x,v/2-1)/
      pow(2,v/2)*gammaFun(v/2)
  }

  // Exponential Distribution
  val exponential = (x: Double) => {
    val lamda = param3
    lamda*exp(-lamda*x)
  }

  // Weibull Distribution u=0,a=1 standard Weibull distribution
  val weibull = (x: Double, u:Double, a: Double, b: Double) => {
    b/a*pow((x-u)/a,b-1)*pow(math.E,-pow((x-u)/a,b))
  }

  // Log normal distribution
  val logNormal = (x: Double) => {
    val lx = log(x)
    pi/x*exp(-lx*lx)
  }

  // Gamma distribution
  val gamma = (x: Double) => {
    val alpha = param2._1
    val lamda = param2._2
    (pow(x,alpha-1)*pow(lamda,alpha)*exp(-lamda*x))/
      (gammaFun(alpha))
  }


  //  // Log Gamma distribution
  //  val logGamma = (x: Double, alpha: Int, beta: Int) =>
  //    exp(beta*x)*exp(-exp(x)/alpha)/
  //      (pow(alpha, beta)*fact(beta-1))

  // Simple computation of m! (for beta)
  def fact(m: Int): Int = if(m < 2) 1 else m*fact(m-1)
  // Normalization factor for Beta
  val cBeta = (n: Int, m: Int) => {
    val f = if(n < 2) 1 else fact(n-1)
    val g = if(m < 2) 1 else fact(m-1)
    f*g/fact(n+m -1).toDouble
  }

  // Beta distribution
  val beta = (x: Double, alpha: Int, beta: Int) =>
    pow(x, alpha-1)*pow(x, beta-1)/cBeta(alpha, beta)

  val pdfs = Map[Int, Double => Double](
    1 -> gaussian,
    2 -> gamma,
    3 -> exponential
    //    4 ->
    //    5 -> logGamma,
    //    6 -> beta,
    //    7 -> ,
    //    8 -> weibull,
    //    9 -> chiSquare,
    //    10 -> t,
    //    11 -> F
  )
  //
  //  val pdfs_broadcast = sc.broadcast[Iterable[Int]](pdfs.map(_._1))
  //  val kl_rdd  = testData.mapPartitions((it:DATASET) => {
  //    val pdfsList = pdfs_broadcast.value.map(
  //      n => pdfs.get(n).get
  //    )
  //    execute(it, pdfsList).iterator
  //  } )
  //
  //  val kl_master = kl_rdd.collect
  //
  //  val divergences = (0 until kl_master.size by pdfs.size)
  //    ./:(Array.fill(pdfs.size)(0.0))( (s, n) => {
  //      (0 until pdfs.size).foreach(j =>
  //        s.update(j, kl_master(n+j)))
  //      s
  //    }).map( _ / kl_master.length)

  //  val pdfsList = pdfs_broadcast.value.map(
  //    n => pdfs.get(n).get
  //  )
  //  execute(testData, pdfsList).iterator

  println(execute1(data.toArray,Rdata.toArray,3))




}