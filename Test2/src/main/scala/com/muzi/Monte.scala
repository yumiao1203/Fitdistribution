package com.muzi

/**转自
  *https://darrenjw.wordpress.com/2014/02/23/parallel-monte-carlo-using-scala/
  */
import java.util.concurrent.ThreadLocalRandom
import scala.math.exp
import scala.annotation.tailrec

object MonteCarlo {

  @tailrec
  def sum(its: Long,acc: Double): Double = {
    if (its==0)
      (acc)
    else {
      val u=ThreadLocalRandom.current().nextDouble()
      sum(its-1,acc+exp(-u*u))
    }
  }

  def main(args: Array[String]) = {
    println("Hello")
    val tic = System.nanoTime()
    val iters=1000000000
    val result=sum(iters,0.0)
    println(result/iters)
    println((System.nanoTime()-tic)/1e9)
    println("Goodbye")
  }

}


object MonteCarlo_New {

  @tailrec
  def sum(its: Long,acc: Double): Double = {
    if (its==0)
      (acc)
    else {
      val u=ThreadLocalRandom.current().nextDouble()
      sum(its-1,acc+exp(-u*u))
    }
  }

  def main(args: Array[String]) = {
    println("Hello")
    val tic = System.nanoTime()
    val N=4
    val iters=1000000000
    val its=iters/N
    val sums=(1 to N).toList map {x => sum(its,0.0)}
    val result=sums.reduce(_+_)
    println(result/iters)
    println((System.nanoTime()-tic)/1e9)
    println("Goodbye")
  }

}


object MonteCarlo_Parallel {

  @tailrec
  def sum(its: Long,acc: Double): Double = {
    if (its==0)
      (acc)
    else {
      val u=ThreadLocalRandom.current().nextDouble()
      sum(its-1,acc+exp(-u*u))
    }
  }

  def main(args: Array[String]) = {
    println("Hello")
    val tic = System.nanoTime()
    val N=4
    val iters=1000000000
    val its=iters/N
    val sums=(1 to N).toList.par map {x => sum(its,0.0)}
    val result=sums.reduce(_+_)
    println(result/iters)
    println((System.nanoTime() - tic)/1e9)
    println("Goodbye")
  }

}



