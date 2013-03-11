object FeatureSetArray {
  def makeArray[T: Manifest](featureSet: FeatureSet): Array[T] = {
    new Array[T](featureSet.nFeatures)
  }
}


/**
 * Simple wrapper binding a FeatureSet to an array, with some utility methods.
 *
 * Particularly useful for defining strongly typed methods on arrays, where the arrays
 * contain data for some FeatureSet
 */
class FeatureSetArray[T: Manifest, +U <: FeatureSet](
  val arr: Array[T],
  val featureSet: U
) {
  def this(featureSet: U) = {
    this(FeatureSetArray.makeArray[T](featureSet), featureSet)
  }
  val size = featureSet.nFeatures
  def apply(idx: Int) = arr(idx)
  def update(idx: Int, v: T) {arr(idx) = v}

}