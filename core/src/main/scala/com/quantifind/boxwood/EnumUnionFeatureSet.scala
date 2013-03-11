package com.quantifind.boxwood

trait EnumUnionFeatureSet extends FeatureSet {
  def enumUnion: EnumUnionCompanion
  var enumStartIdx: Int = _
  abstract override def setOffsetIndex(idx: Int) = {
    enumStartIdx = idx
    super.setOffsetIndex(idx + enumUnion.nEnums)
  }
}
