import org.apache.spark.SparkContext
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.sql.SparkSession

/**
  * Created by josh on 17-5-25.
  */
object InitKmeans{
  val sc = new SparkContext("local[3]","AppName")
  val spark = SparkSession.builder
    .master("local")
    .appName("Spark read csv")
    .getOrCreate
  val filePath = "./Test1/data/train.csv"
  val trainDF = spark.read
    .format("csv")
    .option("inferSchema", "true")
    .option("header", "true")
    .load(filePath).cache()
  //get the size of matrix
  val m = trainDF.count()
  val n = trainDF.columns.length


  def main(args: Array[String]) {
    print(m, n)
  }
}

