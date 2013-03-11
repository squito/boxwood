package com.quantifind.boxwood

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class FeatureSetTest extends FunSuite with ShouldMatchers  {
  test("multi-mixin indexes") {

    {
      val f1 = new BaseFeatureSet with Gender with Age
      f1.nFeatures should be (8)
      f1.ageStartIdx should be (0)
      f1.genderStartIdx should be (6)
      f1.baby should be (0)
      f1.male should be (6)
      f1.female should be (7)
    }

    {
      val f2 = new BaseFeatureSet with Age with Gender
      f2.nFeatures should be (8)
      f2.ageStartIdx should be (2)
      f2.genderStartIdx should be (0)
      f2.baby should be (2)
      f2.male should be (0)
      f2.female should be (1)
    }

    {
      val f3 = new BaseFeatureSet with Gender with MusicStyles
      f3.nFeatures should be (5)
      f3.genderStartIdx should be (3)
      f3.musicStylesStartIdx should be (0)
      f3.male should be (3)
      f3.female should be (4)
      f3.classical should be (0)
      f3.rock should be (1)
      f3.country should be (2)
    }
  }
}

trait Gender extends FeatureSet {
  var genderStartIdx: Int = _
  abstract override def setOffsetIndex(idx: Int) = {
    genderStartIdx = idx
    super.setOffsetIndex(idx + 2)
  }

  def male = genderStartIdx
  def female = genderStartIdx + 1
}

trait Age extends FeatureSet {
  var ageStartIdx: Int = _
  abstract override def setOffsetIndex(idx: Int) = {
    ageStartIdx = idx
    super.setOffsetIndex(idx + 6)
  }
  def baby = ageStartIdx
  def child = ageStartIdx + 1
  def teenager = ageStartIdx + 2
  def youngAdult = ageStartIdx + 3
  def middleAged = ageStartIdx + 4
  def old = ageStartIdx + 5

}

trait MusicStyles extends FeatureSet {
  var musicStylesStartIdx: Int = _
  abstract override def setOffsetIndex(idx: Int) = {
    musicStylesStartIdx = idx
    super.setOffsetIndex(idx + 3)
  }
  def classical = musicStylesStartIdx
  def rock = musicStylesStartIdx + 1
  def country = musicStylesStartIdx + 2
}