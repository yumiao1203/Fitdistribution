/**
  * Created by josh on 17-5-24.
  */
import hex.genmodel.algos.glrm.{GlrmInitialization, GlrmRegularizer}
import hex.glrm.GLRMModel.GLRMParameters
import hex.glrm.GLRM
import org.apache.spark.SparkContext
import org.apache.spark.h2o._
import org.apache.spark.sql.SparkSession
import java.util

import scalaj.http.{Http,HttpOptions}

// Create SQL support

object titanic extends App{
  val sc = new SparkContext("local[3]","AppName")
  val spark = SparkSession.builder
    .master("local")
    .appName("Spark read csv")
    .getOrCreate

  val h2oContext = H2OContext.getOrCreate(sc)
  val filePath = "./Test1/data/4_manual_filter_outlier.RData"
  val trainDF = spark.read
    .format("csv")
    .option("inferSchema", "true")
    .option("header", "true")
    .load(filePath).cache()
  val hf: H2OFrame = h2oContext.asH2OFrame(trainDF)

  val glrmParams = new GLRMParameters
  glrmParams._train = hf.key
  glrmParams._init = GlrmInitialization.PlusPlus
  glrmParams._regularization_x = GlrmRegularizer.L2
  glrmParams._regularization_y = GlrmRegularizer.L1
  glrmParams._k = 8
  val numCols = trainDF.columns.length

  //for(k <- math.floor(1/3*numCols) to math.floor(2/3*numCols)){
    for(j <- 0.1 to 1 by 0.1){
      glrmParams._gamma_x = j
      for(i <- 0.1 to 1 by 0.1) {
        glrmParams._gamma_y = i
        val glrm = new GLRM(glrmParams).trainModel().get()
      }
    }
 // }

//  val glrmAddGrid = new ParamGridBuilder
//
//
//
//  println(glrm._output._history_objective)

  // Shutdown H2O
  //    h2oContext.stop() // there is a bug because `DefaultHttpClient` is deprecated
  val stop_success = 200 == Http("http://localhost:54321/3/Shutdown")
    .postData("")
    .option(HttpOptions.readTimeout(10000))
    .execute().code

  if (stop_success) {
    println("H2O stoped.")
  }


















}

