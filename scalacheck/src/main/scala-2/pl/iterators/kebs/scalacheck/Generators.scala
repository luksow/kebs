package pl.iterators.kebs.scalacheck

import org.scalacheck.Arbitrary
import pl.iterators.kebs.scalacheck.CommonArbitrarySupport

trait Generator[T] extends CommonArbitrarySupport {
  def ArbT: Arbitrary[T]

  def generate: T = ArbT.arbitrary.sample.get
}

trait AllGenerators[T] {

  val normal: Generator[T]

  val minimal: Generator[T]

  val maximal: Generator[T]
}
