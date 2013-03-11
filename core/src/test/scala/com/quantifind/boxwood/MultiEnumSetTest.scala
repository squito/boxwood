package com.quantifind.boxwood

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class MultiEnumSetTest extends FunSuite with ShouldMatchers {

  test("multi enums") {
    Union_A_B.nEnums should be (A.values().length + B.values().length)
    Union_A_B.enumToIdx(A.Foo) should be (A.Foo.ordinal())
    Union_A_B.enumToIdx(A.Bar) should be (A.Bar.ordinal())
    Union_A_B.enumToIdx(B.X) should be (2 + B.X.ordinal())
    Union_A_B.enumToIdx(B.Y) should be (2 + B.Y.ordinal())

    //this version isn't type safe -- this compiles, but fails (since C isn't in the union)
    evaluating {Union_A_B.enumToIdx(C.Ooga)} should produce [Exception]

    //now try a type-safe version

    import Union_A_B._  //why doesn't bring in the implicits?
    implicit object AWitness extends Union_A_B[A]
    implicit object BWitness extends Union_A_B[B]
    A.values().foreach {a => getIdx(a) should be (a.ordinal())}
    B.values().foreach {b => getIdx(b) should be (b.ordinal() + 2)}

    //this version won't even compile
    //getIdx(C.Ooga)
  }
}


class Union_A_B[T <: Enum[T]]

object Union_A_B extends EnumUnionCompanion {
  implicit object AWitness extends Union_A_B[A] //not sure why, these are useless when defined here
  implicit object BWitness extends Union_A_B[B]

  val enumClasses = Seq(classOf[A], classOf[B])

  //can't figure out how to lift this function to the trait ...
  def getIdx[T <: Enum[T]: Union_A_B](x: T): Int = enumToIdx(x.asInstanceOf[Enum[_]])
}
