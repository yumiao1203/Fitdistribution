import breeze.linalg.norm
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.Statistics

/**
  * Created by josh on 17-5-26.
  */
object fitTest extends App {
  val vec = Vectors.dense(0.1, 0.15, 0.2, 0.3, 0.25)
  val goodnessOfFitTestResult = Statistics.chiSqTest(vec)
  println(s"$goodnessOfFitTestResult\n")

}
