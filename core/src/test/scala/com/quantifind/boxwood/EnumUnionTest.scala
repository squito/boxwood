package com.quantifind.boxwood

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

/** this defines a union over enums A & B */
class Union_A_B[T <: Enum[T]] extends EnumUnion[T]
object Union_A_B extends EnumUnionCompanion[Union_A_B[_ <: Enum[_]]] {
  //for these implicits to work, they must be defined *above* their use (if in the same source file)
  implicit object AWitness extends Union_A_B[A]
  implicit object BWitness extends Union_A_B[B]

  val enumClasses = Seq(classOf[A], classOf[B])
}

class Union_B_C[T <: Enum[T]] extends EnumUnion[T]
object Union_B_C extends EnumUnionCompanion[Union_B_C[_ <: Enum[_]]] {
  implicit object BWitness extends Union_B_C[B]
  implicit object CWitness extends Union_B_C[C]

  val enumClasses = Seq(classOf[B], classOf[C])
}

class EnumUnionTest extends FunSuite with ShouldMatchers {

  test("multi enums") {
    Union_A_B.nEnums should be (A.values().length + B.values().length)
    Union_A_B.enumToIdx(A.Foo) should be (A.Foo.ordinal())
    Union_A_B.enumToIdx(A.Bar) should be (A.Bar.ordinal())
    Union_A_B.enumToIdx(B.X) should be (2 + B.X.ordinal())
    Union_A_B.enumToIdx(B.Y) should be (2 + B.Y.ordinal())

    //this version isn't type safe -- this compiles, but fails (since C isn't in the union)
    evaluating {Union_A_B.enumToIdx(C.Ooga)} should produce [Exception]

    //now try a type-safe version
    import Union_A_B._
    A.values().foreach {a => getIdx(a) should be (a.ordinal())}
    B.values().foreach {b => getIdx(b) should be (b.ordinal() + 2)}

    import shapeless.test.illTyped

    //this version won't even compile
    illTyped("""
      getIdx(C.Ooga)
    """)
    //We can have multiple enum unions exist side by side
    import Union_B_C._
    B.values().foreach {b => Union_B_C.getIdx(b) should be (b.ordinal())}
    C.values().foreach {c => Union_B_C.getIdx(c) should be (c.ordinal() + 2)}
    //Though A exists in some union type, Union_B_C still doesn't know about it, so this won't compile
    illTyped("""
      A.values().foreach {a => Union_B_C.getIdx(a) should be (a.ordinal())}
    """)
  }
}


