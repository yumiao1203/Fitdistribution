import breeze.linalg.{DenseVector => BDV}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors

/**
  * Created by josh on 17-5-26.
  */
object FitDistribution {
  def main(args: Array[String]) {
  }
  val distNames = List("beta", "exponential", "normal", "gamma",  "weibull")

  def fitNormal(vec: BDV[Double]): Unit = {

  }
  def fitBeta(vec: BDV[Double]): Unit = {

  }

  val vec = Vectors.dense(0.1, 0.15, 0.2, 0.3, 0.25)
  val PD = new BDV[Double](vec.size)
  var indx:Int = 1
//
//
//
//
//
//  for (indx <- 1 to distNames.length) {
//    val dname = distNames(indx)
//    dname match {
//      case "normal" => PD = fitNormal(vec)
//      case "beta" => PD = fitBeta(vec)
//    }
//  }
}
