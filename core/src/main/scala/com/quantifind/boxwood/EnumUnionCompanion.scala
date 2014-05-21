package com.quantifind.boxwood


/** used for defining a union type over multiple enums, see EnumUnionCompanion */
trait EnumUnion[+T <: Enum[_]]

/**
 * Defines a union type over multiple enums.  For each union type, create an object which
 * extends this trait, and
 * (1) for each enum `E` define an implicit object that extends EnumUnion[`E`]
 * (2) add each enum class `E` to enumClasses
 */
trait EnumUnionCompanion[E <: EnumUnion[Enum[_]]] {
  val enumClasses: Seq[Class[_ <: Enum[_]]]
  lazy val enums: Seq[_ <: Enum[_]] = enumClasses.flatMap(_.getEnumConstants.toSeq)
  private[boxwood] lazy val enumToIdx: Map[Enum[_], Int] = enums.zipWithIndex.toMap
  private[boxwood] lazy val idxToEnum: Array[Enum[_]] = enums.toArray

  /** the number of enum *constants* over all enums in this union type */
  def nEnums = enumToIdx.values.max + 1
  /** given an enum in this union type, get a unique index for it, from 0 to nEnums - 1 */
  def getIdx[T <: Enum[T]](x: T)(implicit evidence: E with EnumUnion[T]): Int = enumToIdx(x.asInstanceOf[Enum[_]])
  def getIdxOfAnyEnum[T <: Enum[_]](x: T): Int = enumToIdx.getOrElse(x, -1)
  def getEnum(idx: Int): Enum[_] = {
    idxToEnum(idx)
  }
}