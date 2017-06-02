import breeze.numerics.{abs, log}

/**
  * Created by josh on 17-5-25.
  */
object KullbackLiebler {

  final val EPS = 1e-10
  type DATASET = Iterator[(Double, Double)]

  def execute(
               xy: DATASET,
               f: Double => Double): Double = {

    val z = xy.filter{ case(x, y) => abs(y) > EPS}
    - z./:(0.0){ case(s, (x, y)) => {
      val px = f(x)
      s + px*log(px/y)}
    }
  }

  def execute(
               xy: DATASET,
               fs: Iterable[Double=>Double]): Iterable[Double] =
    fs.map(execute(xy, _))

}

