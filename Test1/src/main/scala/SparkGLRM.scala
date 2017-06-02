
import breeze.linalg.{DenseVector => BDV}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Generalized Low Rank Models for Spark
  *
  * Run these commands from the spark root directory.
  *
  * Compile with:
  * sbt/sbt assembly
  *
  * Run with:
  * ./bin/spark-submit  --class org.apache.spark.examples.SparkGLRM  \
  * ./examples/target/scala-2.10/spark-examples-1.1.0-SNAPSHOT-hadoop1.0.4.jar \
  * --executor-memory 1G \
  * --driver-memory 1G
  */
class SparkGLRM {}
object SparkGLRM {
  /*********************************
    * GLRM: Bank of loss functions
    *********************************/
  def lossL2squaredGrad(i: Int, j: Int, prediction: Double, actual: Double): Double = {
    prediction - actual
  }

  def lossL1Grad(i: Int, j: Int, prediction: Double, actual: Double): Double = {
    // a subgradient of L1
    math.signum(prediction - actual)
  }

  def mixedLossGrad(i: Int, j: Int, prediction: Double, actual: Double): Double = {
    // weird loss function subgradient for demonstration
    if (i + j % 2 == 0) lossL1Grad(i, j, prediction, actual) else lossL2squaredGrad(i, j, prediction, actual)
  }

  /***********************************
    * GLRM: Bank of prox functions
    **********************************/
  // L2 prox
  def proxL2(v:BDV[Double], stepSize:Double, regPen:Double): BDV[Double] = {
    val arr = v.toArray.map(x => x / (1.0 + stepSize * regPen))
    new BDV[Double](arr)
  }

  // L1 prox
  def proxL1(v:BDV[Double], stepSize:Double, regPen:Double): BDV[Double] = {
    val sr = regPen * stepSize
    val arr = v.toArray.map(x =>
      if (math.abs(x) < sr) 0
      else if (x < -sr) x + sr
      else x - sr
    )
    new BDV[Double](arr)
  }

  // Non-negative prox
  def proxNonneg(v:BDV[Double], stepSize:Double, regPen:Double): BDV[Double] = {
    val arr = v.toArray.map(x => math.max(x, 0))
    new BDV[Double](arr)
  }

  /* End of GLRM libarry */


  // Helper functions for updating
  def computeLossGrads(ms: Broadcast[Array[BDV[Double]]], us: Broadcast[Array[BDV[Double]]],
                       R: RDD[(Int, Int, Double)],
                       lossGrad: (Int, Int, Double, Double) => Double) : RDD[(Int, Int, Double)] = {
    R.map { case (i, j, rij) => (i, j, lossGrad(i, j, ms.value(i).dot(us.value(j)), rij))}
  }

  // Update factors
  def update(us: Broadcast[Array[BDV[Double]]], ms: Broadcast[Array[BDV[Double]]],
             lossGrads: RDD[(Int, Int, Double)], stepSize: Double,
             nnz: Array[Double],
             prox: (BDV[Double], Double, Double) => BDV[Double], regPen: Double)
  : Array[BDV[Double]] = {
    val rank = ms.value(0).length
    val ret = Array.fill(ms.value.size)(BDV.zeros[Double](rank))

    val retu = lossGrads.map { case (i, j, lossij) => (i, us.value(j) * lossij) } // vector/scalar multiply
      .reduceByKey(_ + _).collect() // vector addition through breeze

    for (entry <- retu) {
      val idx = entry._1
      val g = entry._2
      val alpha = (stepSize / (nnz(idx) + 1))

      ret(idx) = prox(ms.value(idx) - g * alpha, alpha, regPen)
    }

    ret
  }

  def fitGLRM(R: RDD[(Int, Int, Double)], M:Int, U:Int,
              lossFunctionGrad: (Int, Int, Double, Double) => Double,
              moviesProx: (BDV[Double], Double, Double) => BDV[Double],
              usersProx: (BDV[Double], Double, Double) => BDV[Double],
              rank: Int,
              numIterations: Int,
              regPen: Double) : (Array[BDV[Double]], Array[BDV[Double]], Array[Double]) = {
    // Transpose data
    val RT = R.map { case (i, j, rij) => (j, i, rij) }.cache()

    val sc = R.context

    // Compute number of nonzeros per row and column
    val mCountRDD = R.map { case (i, j, rij) => (i, 1) }.reduceByKey(_ + _).collect()
    val mCount = Array.ofDim[Double](M)
    for (entry <- mCountRDD)
      mCount(entry._1) = entry._2
    val maxM = mCount.max
    val uCountRDD = R.map { case (i, j, rij) => (j, 1) }.reduceByKey(_ + _).collect()
    val uCount = Array.ofDim[Double](U)
    for (entry <- uCountRDD)
      uCount(entry._1) = entry._2
    val maxU = uCount.max

    // Initialize m and u
    var ms = Array.fill(M)(BDV[Double](Array.tabulate(rank)(x => math.random / (M * U))))
    var us = Array.fill(U)(BDV[Double](Array.tabulate(rank)(x => math.random / (M * U))))

    // Iteratively update movies then users
    var msb = sc.broadcast(ms)
    var usb = sc.broadcast(us)

    val stepSize = 1.0

    val errs = Array.ofDim[Double](numIterations)

    for (iter <- 1 to numIterations) {
      println("Iteration " + iter + ":")

      // Update ms
      println("Computing gradient losses")
      var lg = computeLossGrads(msb, usb, R, lossFunctionGrad)
      println("Updating M factors")
      ms = update(usb, msb, lg, stepSize, mCount, moviesProx, regPen)
      msb = sc.broadcast(ms) // Re-broadcast ms because it was updated

      // Update us
      println("Computing gradient losses")
      lg = computeLossGrads(usb, msb, RT, lossFunctionGrad)
      println("Updating U factors")
      us = update(msb, usb, lg, stepSize, uCount, usersProx, regPen)
      usb = sc.broadcast(us) // Re-broadcast us because it was updated

      errs(iter - 1) = math.sqrt(R.map { case (i, j, rij) =>
        val err = ms(i).dot(us(j)) - rij
        err * err
      }.mean())

      println(s"RMSEs at iteration $iter: " + errs.mkString(", "))
    }


    (msb.value, usb.value, errs)
  }


  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setAppName("SparkGLRM").setMaster("local[2]")
    val sc = new SparkContext(sparkConf)

    // Number of movies
    val M = 1000
    // Number of users
    val U = 1000
    // Number of non-zeros per row
    val NNZ = 10
    // Number of features
    val rank = 2
    // Number of iterations
    val numIterations = 100
    // regularization parameter
    val regPen = 0.001

    // Number of partitions for data, set to number of cores in cluster
    val numChunks = 4
    // Build non-zeros
    val R = sc.parallelize(0 until M, numChunks).flatMap{i =>
      val inds = new scala.collection.mutable.TreeSet[Int]()
      while (inds.size < NNZ) {
        inds += scala.util.Random.nextInt(U)
      }
      inds.toArray.map(j => (i, j, scala.math.random))
    }.cache()

    printf("Running with M=%d, U=%d, nnz=%d, rank=%d, iters=%d, regPen=%f\n",
      M, U, NNZ, rank, numIterations, regPen)

    // Fit GLRM
    val (ms, us, errs) = fitGLRM(R, M, U, lossL2squaredGrad, proxL2, proxL2, rank, numIterations, regPen)

    sc.stop()
  }
}



