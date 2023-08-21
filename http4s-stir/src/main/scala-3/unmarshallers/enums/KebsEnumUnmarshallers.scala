package pl.iterators.kebs.unmarshallers.enums

import pl.iterators.stir.unmarshalling.PredefinedFromStringUnmarshallers._
import pl.iterators.stir.unmarshalling.{FromStringUnmarshaller, Unmarshaller}
import cats.effect.IO
import pl.iterators.kebs.macros.enums.{EnumOf, ValueEnumOf}
import pl.iterators.kebs.enums.ValueEnum
import scala.reflect.Enum
import scala.reflect.ClassTag

trait EnumUnmarshallers {
  final def enumUnmarshaller[E <: Enum](using e: EnumOf[E]): FromStringUnmarshaller[E] = Unmarshaller { name =>
    e.`enum`.values.find(_.toString().toLowerCase() == name.toLowerCase()) match {
      case Some(enumEntry) => IO.pure(enumEntry)
      case None =>
        IO.raiseError(new IllegalArgumentException(s"""Invalid value '$name'. Expected one of: ${e.`enum`.values.mkString(", ")}"""))
    }
  }

  given kebsEnumUnmarshaller[E <: Enum](using e: EnumOf[E]): FromStringUnmarshaller[E] =
    enumUnmarshaller
}

trait ValueEnumUnmarshallers extends EnumUnmarshallers {
  final def valueEnumUnmarshaller[V, E <: ValueEnum[V] with Enum](using `enum`: ValueEnumOf[V, E], cls: ClassTag[V]): Unmarshaller[V, E] = Unmarshaller { v =>
    `enum`.`enum`.values.find(e => e.value == v) match {
      case Some(enumEntry) => IO.pure(enumEntry)
      case None =>
        IO.raiseError(new IllegalArgumentException(s"""Invalid value '$v'. Expected one of: ${`enum`.`enum`.values.map(_.value).mkString(", ")}"""))
    }
  }

  given kebsValueEnumUnmarshaller[V, E <: ValueEnum[V] with Enum](using `enum`: ValueEnumOf[V, E], cls: ClassTag[V]): Unmarshaller[V, E] =
    valueEnumUnmarshaller

  given kebsIntValueEnumFromStringUnmarshaller[E <: ValueEnum[Int] with Enum](using ev: ValueEnumOf[Int, E]): FromStringUnmarshaller[E] =
    intFromStringUnmarshaller andThen valueEnumUnmarshaller
  given kebsLongValueEnumFromStringUnmarshaller[E <: ValueEnum[Long] with Enum](using ev: ValueEnumOf[Long, E]): FromStringUnmarshaller[E] =
    longFromStringUnmarshaller andThen valueEnumUnmarshaller
  given kebsShortValueEnumFromStringUnmarshaller[E <: ValueEnum[Short] with Enum](
      using ev: ValueEnumOf[Short, E]): FromStringUnmarshaller[E] =
    shortFromStringUnmarshaller andThen valueEnumUnmarshaller
  given kebsByteValueEnumFromStringUnmarshaller[E <: ValueEnum[Byte] with Enum](using ev: ValueEnumOf[Byte, E]): FromStringUnmarshaller[E] =
    byteFromStringUnmarshaller andThen valueEnumUnmarshaller
}

trait KebsEnumUnmarshallers extends ValueEnumUnmarshallers {}
