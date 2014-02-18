package org.denigma.quests.nlp

/**
 * Created with IntelliJ IDEA.
 * Member: AntonKulaga
 * Date: 1/19/13
 * Time: 3:51 PM
 * To change this template use File | Settings | File Templates.
 */
class HyperTraits {

}

trait HyperNode
{
  /**check if it contains the field*/
  def contains(str:String)

  /**Gets the field inside oneself*/
  def field[RET](str:String)

  def relation[RET](str:String):Iterator[RET]

}
