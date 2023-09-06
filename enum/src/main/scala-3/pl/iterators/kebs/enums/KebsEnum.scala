package pl.iterators.kebs.enums

import pl.iterators.kebs.enums.ValueEnum
import scala.quoted._
import scala.compiletime.{constValue, erasedValue, error, summonInline}
import scala.deriving.Mirror
import scala.reflect.{ClassTag, Enum}

trait KebsEnum {
  given [E <: Enum]: EnumLike[E] = macro EnumEntryMacros.enumOfImpl[E]
}

inline private def widen[A, B] (a: A): A & B =
  inline a match {
    case b: B => b
  }

class EnumEntryMacros {
  def enumOfImpl[E <: Enum](using m: Mirror.SumOf[E], ct: ClassTag[E]): EnumOf[E] = {
    val enumValues = summonCases[m.MirroredElemTypes, E]
    EnumOf[E](new EnumLike[E] {
      override def values: Array[E] = enumValues.toArray
    })
  }

  inline private def summonCases[T <: Tuple, A]: List[A] =
    inline erasedValue[T] match {
    case _: (h *: t) =>
      (inline summonInline[Mirror.Of[h]] match {
        case m: Mirror.Singleton =>
          widen[m.MirroredMonoType, A](m.fromProduct(EmptyTuple)) :: summonCases[t, A]
        case x => error("Enums cannot include parameterized cases.")
      })

    case _: EmptyTuple => Nil
  }
}