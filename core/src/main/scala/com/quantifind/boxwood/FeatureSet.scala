package com.quantifind.boxwood

trait FeatureSet extends Serializable {
  /**
   * takes in the starting index for this feature set.
   *
   * Implementations *must* be abstract overrides, and *must* call super.  The call to super
   * should increment the index by the number of features in this feature set, and should
   * return the value from the call to super
   *
   * @param index
   * @return
   */
  def setOffsetIndex(index: Int): Int

  def nFeatures: Int
}


class BaseFeatureSet extends FeatureSet {
  var nFeatures: Int = _
  override def setOffsetIndex(index: Int) = {
    nFeatures = index
    index
  }
  setOffsetIndex(0)
}
