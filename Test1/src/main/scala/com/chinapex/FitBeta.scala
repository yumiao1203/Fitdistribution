package com.chinapex

import breeze.linalg.{max, min, sum, DenseMatrix => BDM, DenseVector => BDV}
import breeze.numerics.{exp, log, pow}
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}


/**
  * Created by josh on 17-6-2.
  * Beta 分布的定义域为(0,1)
  */
object FitBeta {
  def main(args: Array[String]): Unit = {
//    val vec = BDV(0.1, 0.1, 0.9, 0.5, 0.5, 0.4, 0.3, 0.3, 0.5, 0.9)
val t2 = System.nanoTime()
val conf = new SparkConf().setAppName("Fit Distribution").setMaster("local")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")

    val spark = SparkSession.builder
      .master("local")
      .appName("Spark CSV Reader")
      .getOrCreate
    val df = spark.read
      .format("com.databricks.spark.csv")
      .option("inferSchema", "true")
      .option("header", "true")
      .load("/home/josh/IdeaProjects/Fitdistribution/r_beta.csv")

    val testData = df.select("x").collect().map{r => r.getDouble(0)}
    val vec = BDV(testData:_*)
    fitBeta(vec)
    val timecost = (System.nanoTime() -t2)/1e9
    println(fitBeta(vec),timecost)
  }

  def fGamma(a: Double): Double = {
    val g = (t: Double) => pow(t, a - 1) * exp(-t)
    val deltaX = 0.01

    def xn = Stream.iterate(0.0)(_ + deltaX)

    def xn1 = xn.takeWhile(_ < 10000)

    def yn1 = xn1.map(g)

    val G = yn1.max
    G
  }

  def deriOfGamma(a: Double, n: Int): Double = {
    val g = (t: Double) => pow(t, a - 1) * exp(-t) * pow(log(t), n)
    val deltaX = 0.01

    def xn = Stream.iterate(0.0)(_ + deltaX)

    def xn1 = xn.takeWhile(_ < 10000)

    def yn1 = xn1.map(g)

    val G = yn1.max
    G
  }

  def psi(a: Double): Double = {
    deriOfGamma(a, 1) / fGamma(a)
  }

  def psiPrime(a: Double): Double = {
    deriOfGamma(a, 2) / fGamma(a) - pow(deriOfGamma(a, 1), 2) / pow(fGamma(a), 2)
  }

  def fitBeta(vec: BDV[Double]): (Double,Double) = {
    val n = vec.length
    var alpha0 = 4.0
    var beta0 = 3.0
    for (i <- 1 to 20) {
      val G = BDM((psiPrime(alpha0) - psiPrime(alpha0 + beta0), -psiPrime(alpha0 + beta0)),
        (-psiPrime(alpha0 + beta0), psiPrime(beta0) - psiPrime(alpha0 + beta0)))
      val g = BDV(psi(alpha0) - psi(alpha0 + beta0) - 1 / n * sum(log(vec)),
        psi(beta0) - psi(alpha0 + beta0) - 1 / n * sum(log(BDV.ones[Double](n) - vec)))
      val delta = G.t * g
      alpha0 += - delta(0)
      beta0  += - delta(1)
    }
    val params =(alpha0,beta0)
    params
  }
}
