import breeze.numerics.{ceil, pow}

val x =1.0
val Ysorted = Array(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6)
var Ynum = Ysorted.length
var Index1 = 0
var Index2 = Ynum -1
var m = 1
val k = 4
var c = 2

do{
  if(x <= Ysorted(ceil(Ynum/c) + Index1)){
    Index2 = ceil(Ynum/c) + Index1
  }else {
    Index1 = ceil(Ynum/c) + Index1
  }
  m = m + 1
  c = pow(2,m)
  Ynum = Index2 - Index1
  println(m, k, c, Index2, Index1, 2*k)
} while((Index2 - Index1) > 2*k)
Index2-Index1