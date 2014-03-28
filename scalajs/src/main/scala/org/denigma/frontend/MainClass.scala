package org.denigma.frontend

import org.denigma.frontend.views._
import rx.core.Var
import org.scalajs.dom

/**
 * Main class of scalajs app
 */
abstract class MainClass extends OrdinaryView("main",dom.document.body)
{


  val isSigned: Var[Boolean] = Var(false)


}
