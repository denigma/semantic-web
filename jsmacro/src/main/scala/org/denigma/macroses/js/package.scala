package org.denigma.macroses
import scala.collection.immutable

/**
 * Useful implicit classes
 */
package object js {
  implicit def asMap[T: ClassToMap](t: T) =  implicitly[ClassToMap[T]].asMap(t)
  implicit def asStringRxMap[T: StringRxMap](t: T) =  implicitly[StringRxMap[T]].asStringRxMap(t)
  implicit def asTagRxMap[T: TagRxMap](t: T) =  implicitly[TagRxMap[T]].asTagRxMap(t)
  implicit def asListRxMap[T: ListRxMap](t: T) =  implicitly[ListRxMap[T]].asListRxMap(t)
  implicit def asBooleanRxMap[T: BooleanRxMap](t: T) =  implicitly[BooleanRxMap[T]].asBooleanRxMap(t)


}
