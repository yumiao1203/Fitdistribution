import breeze.stats.distributions.{Dirichlet, Gaussian}
import breeze.linalg.{DenseVector => BDV}
import breeze.stats._



/**
  * Created by josh on 17-5-26.
  */
object FitDistTest{
  def main(args: Array[String]) {
  }

  // Gaussian distribution parameters estimation
  val normal = Gaussian(0.0, 1.0)
  normal.draw()  // 随机生成一个
  normal.probability(0.0,100.0) //取值在0.0到100.0中概率
  val samples = normal.sample(100)
  val ss = Gaussian.SufficientStatistic(samples.size,mean(samples), samples.size*variance(samples))
  val parm = Gaussian.mle(ss)
  println(parm)

  println(normal.inverseCdf(0.3)) //
  val samplesMean = samples.sum/samples.length
  samples map {normal.logPdf(_)}
  val m = breeze.stats.mean(samples)
  val v = breeze.stats.variance(samples)
  print("mean:",m,"variance:",v)
  val vec = BDV(0.1, 0.1, 0.0, 1.2, -0.8, 1.0)

  //



 //Dirichlet distribution
  val data = Seq(BDV(0.1, 0.1, 0.8),
    BDV(0.2, 0.3, 0.5),
    BDV(0.5, 0.1, 0.4),
    BDV(0.3, 0.3, 0.4)
  )

  val expFam = new Dirichlet.ExpFam(BDV.zeros[Double](3))
  val suffStat = data.foldLeft(expFam.emptySufficientStatistic){(a, x) =>
    a + expFam.sufficientStatisticFor(x)
  }
  val alphaHat = expFam.mle(suffStat)
  print(alphaHat)


}
