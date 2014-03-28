package org.denigma.macroses.js
import scala.collection.immutable._
import scala.language.experimental.macros
import rx._
import org.scalajs.dom.Attr
import scala.reflect.runtime.universe._
import scala.reflect.macros.Context
import scalatags.HtmlTag
/**
 * Just some experiments
 */

/**
 * Trait for materialization
 * @tparam T
 */
trait ClassToMap[T] {
  def asMap(t: T): Map[String,Any]
}


object ClassToMap extends BinderObject {
  implicit def materialize[T]: ClassToMap[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context): c.Expr[ClassToMap[T]] = {
    import c.universe._
    val mapExpr = extract[T,Any](c)

    reify {
      new ClassToMap[T] {
        def asMap(t: T) = mapExpr.splice
      }
    }
  }

}


trait StringRxMap[T] {
  def asStringRxMap(t: T): Map[String,Rx[String]]
}

object StringRxMap extends BinderObject {
  implicit def materialize[T]: StringRxMap[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context): c.Expr[StringRxMap[T]] = {
    import c.universe._
    val mapExpr = extract[T,Rx[String]](c)

    reify {
      new StringRxMap[T] {
        def asStringRxMap(t: T) = mapExpr.splice
      }
    }
  }
}

trait BooleanRxMap[T] {
  def asBooleanRxMap(t: T): Map[String,Rx[Boolean]]
}

object BooleanRxMap extends BinderObject {
  implicit def materialize[T]: BooleanRxMap[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context): c.Expr[BooleanRxMap[T]] = {
    import c.universe._
    val mapExpr = extract[T,Rx[Boolean]](c)

    reify {
      new BooleanRxMap[T] {
        def asBooleanRxMap(t: T) = mapExpr.splice
      }
    }
  }
}



trait TagRxMap[T] {
  def asTagRxMap(t: T): Map[String,Rx[scalatags.HtmlTag]]
}

object TagRxMap extends BinderObject {
  implicit def materialize[T]: TagRxMap[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context): c.Expr[TagRxMap[T]] = {
    import c.universe._
    val mapExpr = extract[T,Rx[scalatags.HtmlTag]](c)

    reify {
      new TagRxMap[T] {
        def asTagRxMap(t: T) = mapExpr.splice
      }
    }
  }
}

trait MapRxMap[T] {
  def asMapRxMap(t: T): Map[String,Rx[Map[String,Any]]]
}

object MapRxMap extends BinderObject
{
  implicit def materialize[T]: MapRxMap[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context): c.Expr[MapRxMap[T]] = {
    import c.universe._
    val mapExpr = extract[T,Rx[Map[String,Any]]](c)

    reify {
      new MapRxMap[T] {
        def asMapRxMap(t: T) = mapExpr.splice
      }
    }
  }
}



trait ListRxMap[T] {
  def asListRxMap(t: T): Map[String,Rx[List[Map[String,Any]]]]
}

object ListRxMap extends BinderObject {
  implicit def materialize[T]: ListRxMap[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context): c.Expr[ListRxMap[T]] = {
    import c.universe._
    val mapExpr = extract[T,Rx[List[Map[String,Any]]]](c)

    reify {
      new ListRxMap[T] {
        def asListRxMap(t: T) = mapExpr.splice
      }
    }
  }


}



class BinderObject  {
  def extract[T: c.WeakTypeTag,TE:  c.WeakTypeTag](c: Context) = {
    import c.universe._

    val mapApply = Select(reify(Map).tree, newTermName("apply"))

    val we = weakTypeOf[TE]

    val pairs = weakTypeOf[T].members.collect {
      case m: MethodSymbol if (m.isVal || m.isCaseAccessor || m.isGetter) && m.returnType.<:<(we) =>
        if(m.returnType.<:<(we)) true
        val name = c.literal(m.name.decoded)
        val value = c.Expr[T](Select(Ident(newTermName("t")), m.name))
        reify(name.splice -> value.splice).tree
    }

    c.Expr[Map[String, TE]](Apply(mapApply, pairs.toList))
  }
}

