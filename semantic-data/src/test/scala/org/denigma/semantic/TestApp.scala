package org.denigma.semantic

import play.api.test.{Helpers, FakeApplication}
import org.specs2.mutable.Around
import org.specs2.specification.Scope
import org.specs2.execute.{Result, AsResult}

class WithSemanticPlugin(val app: FakeApplication = FakeApplication(additionalPlugins = List("org.denigma.semantic.SemanticPlugin"))) extends Around with Scope {
  implicit def implicitApp = app
  override def around[T: AsResult](t: => T): Result = {
    Helpers.running(app)(AsResult.effectively(t))
  }
}