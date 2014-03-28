package org.denigma.binding

/**
 * Useful implicit classes
 */
package object macroses {
  implicit def asMap[T: ClassToMap](t: T) =  implicitly[ClassToMap[T]].asMap(t)
  implicit def asStringRxMap[T: StringRxMap](t: T) =  implicitly[StringRxMap[T]].asStringRxMap(t)
  implicit def asTagRxMap[T: TagRxMap](t: T) =  implicitly[TagRxMap[T]].asTagRxMap(t)
  implicit def asListRxMap[T: ListRxMap](t: T) =  implicitly[ListRxMap[T]].asListRxMap(t)
  implicit def asBooleanRxMap[T: BooleanRxMap](t: T) =  implicitly[BooleanRxMap[T]].asBooleanRxMap(t)

  implicit def asEventMap[T: EventMap](t: T) =  implicitly[EventMap[T]].asEventMap(t)
  implicit def asMouseEventMap[T: MouseEventMap](t: T) =  implicitly[MouseEventMap[T]].asMouseEventMap(t)
  implicit def asTextEventMap[T: TextEventMap](t: T) =  implicitly[TextEventMap[T]].asTextEventMap(t)
  implicit def asKeyEventMap[T: KeyEventMap](t: T) =  implicitly[KeyEventMap[T]].asKeyEventMap(t)
  implicit def asUIEventMap[T: UIEventMap](t: T) =  implicitly[UIEventMap[T]].asUIEventMap(t)
  implicit def asWheelEventMap[T: WheelEventMap](t: T) =  implicitly[WheelEventMap[T]].asWheelEventMap(t)
  implicit def asFocusEventMap[T: FocusEventMap](t: T) =  implicitly[FocusEventMap[T]].asFocusEventMap(t)




}
