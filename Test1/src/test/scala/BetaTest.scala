
import breeze.numerics.{abs, log}
import com.chinapex.FitBeta.{psi, psiPrime}
import breeze.linalg.{max, sum, DenseMatrix => BDM, DenseVector => BDV}


/**
  * Created by josh on 17-6-2.
  */
object BetaTest {
  def main(args: Array[String]): Unit = {
    val vector = BDV(0.1, 0.2, 0.1, 0.2, 0.6,0.3)
    fitBeta(vector)
    println(fitBeta(vector))
  }

  def fitBeta(vec: BDV[Double]): (Double,Double) = {
    val n = vec.length
    var alpha0 = 3.0
    var beta0 = 2.0
    val delta = BDV(1.0,1.0)
    do {
      val G = BDM((psiPrime(alpha0) - psiPrime(alpha0 + beta0), -psiPrime(alpha0 + beta0)),
        (-psiPrime(alpha0 + beta0), psiPrime(beta0) - psiPrime(alpha0 + beta0)))
      val g = BDV(psi(alpha0) - psi(alpha0 + beta0) - 1 / n * sum(log(vec)),
        psi(beta0) - psi(alpha0 + beta0) - 1 / n * sum(log(BDV.ones[Double](n) - vec)))
      val delta = G.t * g
      alpha0 += - delta(0)
      beta0  += - delta(1)
      } while (max(abs(delta)) > 0.5)
  val params =(alpha0,beta0)
    params
  }
}
