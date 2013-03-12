package com.quantifind.boxwood

trait Union_A_B[T <: Enum[T]]

trait EnumUnionCompanion {
  val enumClasses: Seq[Class[_ <: Enum[_]]]
  lazy val enumToIdx: Map[Enum[_], Int] = enumClasses.flatMap(_.getEnumConstants.toSeq).zipWithIndex.toMap

  def nEnums = enumToIdx.values.max + 1
  def getIdx[T <: Enum[T]: Union_A_B](x: T): Int = enumToIdx(x.asInstanceOf[Enum[_]])
}