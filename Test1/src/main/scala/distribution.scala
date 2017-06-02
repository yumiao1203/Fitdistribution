import breeze.numerics.{exp, log, pow, sqrt}

/**
  * Created by josh on 17-5-25.
  */
class Distribution {}
object Distribution {

  // One Gaussian distribution
  val pi = math.Pi
  val gauss = (x: Double, u: Double, d: Double) => {
    exp(-pow((x-u),2)/(2*d*d))/(d*pow(2*pi,1/2))
  }

  // Uniform distribution
  val uniform = (x: Double) => x

  //Cauchy distribution
  val cauchy = (x: Double, a: Double, b: Double) =>
  1/pi*b/((x-a)*(x-a)-b*b)

  //t distribution
  val t = (x: Double, v: Double) => {
    def betaFun (a: Double, b: Double): Double = {
      //def f: Double => Double = (pow(_,a-1))*(pow(1-(_),b-1))
      val f = (i:Double) => (pow(i,a-1))*(pow(1-i,b-1))
      val deltaX = 0.01
      def xn = Stream.iterate(0.0)(_ + deltaX)
      def xn1 = xn.takeWhile(_ < 1)
      def yn1  = xn1.map(f)
      val B = yn1.max
     B
    }
    val B = betaFun(0.5,0.5*v)
    pow((1+x*x/v),(-(v+1)/2)) / (B*sqrt(v))
  }

  def gammaFun (a: Double): Double = {
    val g = (t:Double) => pow(t,a-1)*pow(math.E,-t)
    val deltaX = 0.01
    def xn = Stream.iterate(0.0)(_ + deltaX)
    def xn1 = xn.takeWhile(_ < 10000)
    def yn1  = xn1.map(g)
    val G = yn1.max
    G
  }

  // F distribution
  val F =(x: Double, v1: Double, v2: Double) => {
    gammaFun((v1+v2)/2)*pow((v1/v2),v1/2)*pow(x,(v1/2-1))/
    gammaFun(v1/2)*gammaFun(v2/2)*pow((1+pow(v1,x)/v2),(v1+v2)/2)
  }

  // Chi-Square distribution x>=0
  val chiSquare = (x: Double, v: Double) => {
    pow(math.E, -x/2)*pow(x,v/2-1)/
    pow(2,v/2)*gammaFun(v/2)
  }

  // Exponential Distribution
  val exponential = (x: Double, u:Double, b: Double) => {
    1/b*pow(math.E,-(x-u)/b)
  }

  // Weibull Distribution u=0,a=1 standard Weibull distribution
  val weibull = (x: Double, u:Double, a: Double, b: Double) => {
    b/a*pow((x-u)/a,b-1)*pow(math.E,-pow((x-u)/a,b))
  }

  // Log normal distribution
  val logNormal = (x: Double) => {
    val lx = log(x)
    pi/x*exp(-lx*lx)
  }

  // Gamma distribution
  val gamma = (x: Double, n: Int) =>
    exp(-x)*pow(x, n)/fact(n)

  // Log Gamma distribution
  val logGamma = (x: Double, alpha: Int, beta: Int) =>
    exp(beta*x)*exp(-exp(x)/alpha)/
      (pow(alpha, beta)*fact(beta-1))

  // Simple computation of m! (for beta)
  def fact(m: Int): Int = if(m < 2) 1 else m*fact(m-1)
  // Normalization factor for Beta
  val cBeta = (n: Int, m: Int) => {
    val f = if(n < 2) 1 else fact(n-1)
    val g = if(m < 2) 1 else fact(m-1)
    f*g/fact(n+m -1).toDouble
  }

  // Beta distribution
  val beta = (x: Double, alpha: Int, beta: Int) =>
    pow(x, alpha-1)*pow(x, beta-1)/cBeta(alpha, beta)


}
