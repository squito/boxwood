package com.quantifind.boxwood

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class EnumUnionFeatureSetTest extends FunSuite with ShouldMatchers {
  test("enumUnionFeatureSet") {
    {
      val f = new BaseFeatureSet with A_B_FeatureSet
      f.nFeatures should be (Union_A_B.nEnums)
      f.enumStartIdx should be (0)
      f.get(A.Foo) should be (f.enumStartIdx + A.Foo.ordinal())
      f.get(B.X) should be (f.enumStartIdx + B.X.ordinal() + A.values().length)
    }

    {
      val f2 = new BaseFeatureSet with A_B_FeatureSet with Gender
      f2.nFeatures should be (Union_A_B.nEnums + 2)
      f2.genderStartIdx should be (0)
      f2.enumStartIdx should be (2)
      f2.get(A.Foo) should be (f2.enumStartIdx + A.Foo.ordinal())
      f2.get(B.X) should be (f2.enumStartIdx + B.X.ordinal() + A.values().length)
    }

    {
      val f3 = new BaseFeatureSet with Age with A_B_FeatureSet
      f3.nFeatures should be (Union_A_B.nEnums + 6)
      f3.enumStartIdx should be (0)
      f3.ageStartIdx should be (4)
      f3.get(A.Foo) should be (f3.enumStartIdx + A.Foo.ordinal())
      f3.get(B.X) should be (f3.enumStartIdx + B.X.ordinal() + A.values().length)
      f3.baby should be (4)
      f3.child should be (5)
    }

    {
      val f = new BaseFeatureSet with A_B_FeatureSet
      val fv = new FeatureSetArray[Double, A_B_FeatureSet](f)
      fv.arr.length should be (f.nFeatures)
      fv.arr(f.get(A.Foo)) = 7
    }
  }

  test("enum methods") {
    {
      //signature is a little crazy, but you can write a method that can deal w/ just one enum from
      // within an enumUnionFeatureSet
      def blah[T <: EnumUnion[Enum[_]]](bFeatureSet: EnumUnionFeatureSet[T])(implicit ev: T with EnumUnion[C]) = {
        bFeatureSet.get(C.Ooga)
      }

      blah(new BaseFeatureSet with B_C_FeatureSet with Age) should be (8)
      blah(new BaseFeatureSet with Gender with B_C_FeatureSet) should be (2)
      //compiler error if you try to get a C from a feature set that doesn't have it
      import shapeless.test.illTyped
      illTyped(
        """
          blah(new BaseFeatureSet with A_B_FeatureSet)
        """)
    }
  }
}

trait A_B_FeatureSet extends EnumUnionFeatureSet[Union_A_B[_ <: Enum[_]]] {
  def enumUnion = Union_A_B //TODO has to be a def -- add this to gotchas in README
}

trait B_C_FeatureSet extends EnumUnionFeatureSet[Union_B_C[_ <: Enum[_]]] {
  def enumUnion = Union_B_C
}