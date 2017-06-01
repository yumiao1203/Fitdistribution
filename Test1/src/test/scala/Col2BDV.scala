/**
  * Created by josh on 17-5-27.
  */

import org.apache.spark.SparkContext
import org.apache.spark.sql. SparkSession
import breeze.linalg.{DenseVector => BDV}
import com.chinapex.FitGamma._
import com.chinapex.FitGaussian._
import com.chinapex.FitExponential._
import com.chinapex.FitLogNormal._


object Col2BDV extends App {

  val sc = new SparkContext("local[3]", "AppName")
  val spark = SparkSession.builder
    .master("local")
    .appName("Spark read csv")
    .getOrCreate
  val filePath = "./Test1/data/train.csv"
  val df = spark.read
    .format("csv")
    .option("inferSchema", "true")
    .option("header", "true")
    .load(filePath)
  df.printSchema()


  val cleandf = df.filter("Age is not null")
  　//column2BDV
  val AgeArray = cleandf.select("Age").rdd.map { r => r.getDouble(0) }.collect()
  val AgeBDV = BDV(AgeArray: _*)

  fitGauss(AgeBDV)
  fitGamma(AgeBDV)
  fitExponential(AgeBDV)
  fitLogNormal(AgeBDV)

}
