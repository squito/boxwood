package com.quantifind.boxwood

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

object TestUnion_A_B extends EnumUnionCompanion {
  //for these implicits to work, they must be defined *above* their use (if in the same source file)
  implicit object AWitness extends Union_A_B[A]
  implicit object BWitness extends Union_A_B[B]

  val enumClasses = Seq(classOf[A], classOf[B])
}

class MultiEnumSetTest extends FunSuite with ShouldMatchers {

  test("multi enums") {
    TestUnion_A_B.nEnums should be (A.values().length + B.values().length)
    TestUnion_A_B.enumToIdx(A.Foo) should be (A.Foo.ordinal())
    TestUnion_A_B.enumToIdx(A.Bar) should be (A.Bar.ordinal())
    TestUnion_A_B.enumToIdx(B.X) should be (2 + B.X.ordinal())
    TestUnion_A_B.enumToIdx(B.Y) should be (2 + B.Y.ordinal())

    //this version isn't type safe -- this compiles, but fails (since C isn't in the union)
    evaluating {TestUnion_A_B.enumToIdx(C.Ooga)} should produce [Exception]

    //now try a type-safe version
    import TestUnion_A_B._
    A.values().foreach {a => getIdx(a) should be (a.ordinal())}
    B.values().foreach {b => getIdx(b) should be (b.ordinal() + 2)}

    //this version won't even compile
    //getIdx(C.Ooga)
  }
}


