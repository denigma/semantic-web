package controllers

import play.api.mvc.RequestHeader
import org.scalax.semweb.rdf.{Res, IRI}

/**
 * UserRequestHeader
 */
trait UserRequestHeader extends RequestHeader{

  def username: Option[IRI]
  def pjax: Option[String]

  def isSigned:Boolean

//  def canCreate(res: Res): Boolean
//  def canRead(res: Res): Boolean
//  def canUpdate(res: Res): Boolean
//  def canDelete(res: Res): Boolean


}
