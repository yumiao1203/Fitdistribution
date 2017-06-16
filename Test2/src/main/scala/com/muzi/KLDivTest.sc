import breeze.linalg.{max, min}
import breeze.numerics.{abs, ceil, log, pow}
import breeze.stats.distributions.Gaussian


val samples1 = Gaussian(0, 1).sample(100).sortBy(x => x).toArray
val samples2 = Gaussian(0,1).sample(100).sortBy(x=>x).toArray
val tic = System.nanoTime()
println(samples1)
samples1.length
execute(2.1, samples1, 10)
log(execute(samples1(10), samples1.drop(10), 10))
samples1.drop(10).length

def execute(x: Double, Ysorted: Array[Double], k: Int): Double = {
  val Ynum = Ysorted.length
  //    assert(k > Ynum, s"k should be less than sample size!")
  var m = 1
  var Index1 = 0
  var Index2 = Ynum - 1
  var c = 2
  do {
    if (x <= Ysorted(ceil(Ynum / c) + Index1)) {
      Index2 = ceil(Ynum / c) + Index1
    } else {
      Index1 = ceil(Ynum / c) + Index1
    }
    m = m + 1
    c = pow(2, m)
  } while (Index2 - Index1 > 2 * k)

  val startPointIndex = max(Index1 - k, 0)
  val endPointIndex = min(Index2 + k, Ynum - 1)
  val difArraySize = endPointIndex - startPointIndex
  val difArray = new Array[Double](difArraySize)
  for (i <- startPointIndex until endPointIndex) {
    difArray(i - startPointIndex) = abs(x - Ysorted(i))
  }
  val nearestPointsIndex = new Array[Double](k)
  var distance: Double = 0.0
  for (l <- 0 until k) {
    val minIndex1 = difArray.zipWithIndex.minBy(_._1)._2 //找到最小值所对应的index
    nearestPointsIndex(l) = minIndex1
    distance = distance + difArray(1)
    difArray(minIndex1) = max(difArray)
  }
  distance/k
}


