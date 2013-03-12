package com.quantifind.boxwood

trait Union_A_B[T <: Enum[T]]

trait EnumUnionCompanion {
  val enumClasses: Seq[Class[_ <: Enum[_]]]
  lazy val enumToIdx: Map[Enum[_], Int] = {
    var idx = 0
    val tmp = collection.mutable.Map[Enum[_], Int]()
    enumClasses.foreach { enumClass =>
      enumClass.getEnumConstants.map{enum =>
        tmp(enum) = idx
        idx += 1
      }
    }
    tmp.toMap
  }

  def nEnums = enumToIdx.values.max + 1
  def getIdx[T <: Enum[T]: Union_A_B](x: T): Int = enumToIdx(x.asInstanceOf[Enum[_]])
}