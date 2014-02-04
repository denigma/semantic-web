package org.denigma.macroses

import scala.language.experimental.macros


trait ModelLike {
  def toMap[T]: Map[String, Any] = macro Model.toMap_impl[T]
}

object Model {
  implicit class Mappable[M <:ModelLike](val model: M) extends AnyVal {
    def asMap: Map[String, Any] = macro Macros.asMap_impl[M]
  }

  import scala.reflect.macros.Context

  def toMap_impl[T: c.WeakTypeTag](c: Context) = {
    import c.universe._

    val mapApply = Select(reify(Map).tree, newTermName("apply"))

    val pairs = weakTypeOf[T].declarations.collect {
      case m: MethodSymbol if m.isVal || m.isCaseAccessor =>
        val name = c.literal(m.name.decoded)
        val value = c.Expr(Select(c.resetAllAttrs(c.prefix.tree), m.name))
        reify(name.splice -> value.splice).tree
    }

    c.Expr[Map[String, Any]](Apply(mapApply, pairs.toList))
  }


}

object Macros {
  import scala.reflect.macros.Context

  def asMap_impl[T: c.WeakTypeTag](c: Context) = {
    import c.universe._

    val mapApply = Select(reify(Map).tree, newTermName("apply"))
    val model = Select(c.prefix.tree, newTermName("model"))

    val pairs = weakTypeOf[T].declarations.collect {
      case m: MethodSymbol if m.isCaseAccessor =>
        val name = c.literal(m.name.decoded)
        val value = c.Expr(Select(model, m.name))
        reify(name.splice -> value.splice).tree
    }

    c.Expr[Map[String, Any]](Apply(mapApply, pairs.toList))
  }
}