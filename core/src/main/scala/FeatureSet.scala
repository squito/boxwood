trait FeatureSet {
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
  def setOffsetIndex(index: Int) : Int

  def show


  val nFeatures = setOffsetIndex(0)
}


class BaseFeatureSet extends FeatureSet {
  override def setOffsetIndex(index: Int) = index
  override def show {}
}
