import breeze.linalg.{DenseVector, norm}
import breeze.optimize.DiffFunction

/**
  * Created by josh on 17-5-27.
  */
object test11 extends App{
  val c = List(3,4,5,6)
  c.length
  c.sum
  c.product
  val d = Vector(1,2,3,4,5)
  d.slice(2,5)
  d.updated(3,0)
  //change vector to dense vector
  val d1 = DenseVector(d.toArray:_*)
  //
  val f = new DiffFunction[DenseVector[Double]] {
    def calculate(x: DenseVector[Double]) = {
      (norm((x - 3d) :^ 2d,1d),(x * 2d) - 6d)
    }
  }
  println(f.valueAt(DenseVector(3,3,3)))



  def g(x: DenseVector[Double]) = (x - 3.0):^ 2.0

}

object Demo {
  def main(args: Array[String]) {
    val it = Iterator(("a", "number"),("of", "words"))
    while (it.hasNext){
      println(it.next())
    }
  }
}