package org.denigma.semantic.model

import org.openrdf.model.impl.LiteralImpl


/*
TODO: do something to avoid confusion
 */
case class Literal(value:String) extends LiteralImpl(value){

}
