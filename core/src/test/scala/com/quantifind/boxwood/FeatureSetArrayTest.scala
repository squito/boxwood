package com.quantifind.boxwood

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class FeatureSetArrayTest extends FunSuite with ShouldMatchers  {
  test("array indexing") {
    val f = new BaseFeatureSet with Gender with MusicStyles
    val vector = FeatureSetArray.makeArray[Double](f)
    vector.length should be (f.nFeatures)
    //make sure we get specialized arrays for primitives
    vector.getClass should be (new Array[Double](0).getClass)
    vector.getClass should not be (new Array[Object](0).getClass)

    //do some normal array operations
    (0 until vector.length).foreach {idx => vector(idx) = idx}
    val v2 = FeatureSetArray.makeArray[Double](f)
    (0 until v2.length).foreach {idx => v2(idx) = idx / 5.0}

    val sum = vector.zipWithIndex.map{case(v,i) => v + v2(i)}
    //now you have typesafe access to the final answer
    sum(f.male) should be (vector(f.male) + v2(f.male))
    sum(f.country) should be (vector(f.country) + v2(f.country))

    //when all vectors have the same feature set, we can make the syntax a little pretty by importing
    // all methods from the feature set
    import f._
    sum(classical) should be (vector(classical) + v2(classical))
  }

  test("featureset type safety") {
    //make some vectors with different feature sets in them, though they both share MusicStyles
    object F1 extends BaseFeatureSet with Gender with MusicStyles
    F1.nFeatures should be (5)
    val v1 = new FeatureSetArray[Double, F1.type](F1)
    v1.arr.length should be (F1.nFeatures)
    v1.arr.getClass should be (new Array[Double](0).getClass)
    object F2 extends BaseFeatureSet with MusicStyles with Age
    F2.nFeatures should be (9)
    val v2 = new FeatureSetArray[Double, F2.type](F2)
    v2.arr.length should be (F2.nFeatures)

    //note that the music style indices are different in both
    F1.musicStylesStartIdx should not be (F2.musicStylesStartIdx)
    F1.country should not be (F2.country)

    //stuff some data into both
    (0 until v1.size).foreach{idx => v1(idx) = idx}
    (0 until v2.size).foreach{idx => v2(idx) = idx * 3.5}

    def rockNCountry(vs: FeatureSetArray[Double, MusicStyles]*) = {
      //because this method access the arrays using the FeatureSetArrays own FeatureSet, it works
      // even though rock & country may be at different indices in each vector
      vs.map{v => v(v.featureSet.country) + v(v.featureSet.rock)}.sum
    }

    //explicit test, just to make sure nothing funny is going on ...
    rockNCountry(Seq(v1, v2):_*) should be (F1.country + F1.rock + (F2.country + F2.rock) * 3.5)

    //reset those values, should still work
    v1(F1.country) = 79.5
    v2(F2.rock) = -53.9
    rockNCountry(Seq(v1, v2):_*) should be (79.5 + F1.rock + F2.country * 3.5 + -53.9)
  }
}
