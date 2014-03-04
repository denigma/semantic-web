package org.denigma.semantic.sparql


/**
 * Class to Insert/Delete values
 */
class Delete extends WithWhere {
  override def stringValue: String = s"DELETE ${WHERE.stringValue}"
}

object INSERT {


}

class Insert extends WithWhere{
  override def stringValue: String = s"INSERT ${WHERE.stringValue}"
}
