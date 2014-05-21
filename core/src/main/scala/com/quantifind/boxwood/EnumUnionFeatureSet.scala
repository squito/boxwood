package com.quantifind.boxwood

trait EnumUnionFeatureSet[E <: EnumUnion[Enum[_]]] extends FeatureSet {
  def enumUnion: EnumUnionCompanion[E]
  var enumStartIdx: Int = _
  abstract override def setOffsetIndex(idx: Int) = {
    enumStartIdx = idx
    super.setOffsetIndex(idx + enumUnion.nEnums)
  }

  def get[T <: Enum[T]](x: T)(implicit evidence: E with EnumUnion[T]): Int = enumStartIdx + enumUnion.getIdx(x)
}
