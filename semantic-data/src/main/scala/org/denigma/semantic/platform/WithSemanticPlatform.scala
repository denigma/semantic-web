package org.denigma.semantic.platform


/*
just a helper that let you do something with db
TODO: refactor in order to get rid of too many dependencies
 */
trait WithSemanticPlatform {


  /*
  semantic platform
   */
  def sp: SemanticPlatform = SP

}
