import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class FeatureSetTest extends FunSuite with ShouldMatchers  {
  test("multi-mixin indexes") {
    val f = new BaseFeatureSet with Gender with Age
    f.nFeatures should be (8)
    f.ageStartIdx should be (0)
    f.genderStartIdx should be (6)
    f.baby should be (0)
    f.male should be (6)
    f.female should be (7)
  }
}

trait Gender extends FeatureSet {
  var genderStartIdx: Int = 0
  abstract override def setOffsetIndex(idx: Int) = {
    println("calling gender w/ " + idx)
    genderStartIdx = idx
    println("genderStartIdx#1 = " + genderStartIdx)
    val r = super.setOffsetIndex(idx + 2)
    println("genderStartIdx#2 = " + genderStartIdx)
    r
  }

  abstract override def show {
    println("genderStartIdx = " + genderStartIdx)
  }

  def male = {
    println("in male, genderStartIdx = " + genderStartIdx)
    genderStartIdx
  }
  def female = genderStartIdx + 1
}

trait Age extends FeatureSet {
  var ageStartIdx: Int = 0
  abstract override def setOffsetIndex(idx: Int) = {
    show
    println("calling age w/ " + idx)
    ageStartIdx = idx
    val r = super.setOffsetIndex(idx + 6)
    show
    r
  }
  abstract override def show {
    super.show
  }

  def baby = ageStartIdx
  def child = ageStartIdx + 1
  def teenager = ageStartIdx + 2
  def youngAdult = ageStartIdx + 3
  def middleAged = ageStartIdx + 4
  def old = ageStartIdx + 5

}

trait MusicStyles extends FeatureSet {
  var musicStylesStartIdx: Int = 0
  abstract override def setOffsetIndex(idx: Int) = {
    musicStylesStartIdx = idx
    super.setOffsetIndex(idx + 3)
  }
  def classical = musicStylesStartIdx
  def rock = musicStylesStartIdx + 1
  def country = musicStylesStartIdx + 2
}

class F1 extends BaseFeatureSet with Gender