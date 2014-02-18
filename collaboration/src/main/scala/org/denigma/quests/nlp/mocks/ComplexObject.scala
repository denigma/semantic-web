package org.denigma.semantic.query.mocks

class UserTest(username:String,usersurname:String) extends ComplexMapper
{
  var props = Map[String,Any]()
  var name:String = register[String]("name",username)
  var surname:String = register[String]("surname",usersurname)
}

/**
 * trait needed for complex binding
 * */
trait ComplexMapper extends ComplexObject
{
  var props:Map[String,Any]
  def contains(key:String) = props.contains(key)
  def get(key: String): Option[Any] = props.get(key)
  def register[RET](key:String, value: RET):RET =
  {
    props = props +( (key,value) )
    value
  }

  def get(source:ComplexObject,key:String): Either[String,ComplexObject] = source.get(key) match
  {
    case Some(value:ComplexObject) => Right(value)
    case Some(str:String) => Left(str)
    case None => null
    case _ => null
  }
}

/**
 * Trait for databinding
 * */
trait ComplexObject
{
  def contains(name:String):Boolean
  def get(key: String): Option[Any]
  def register[RET](key:String, value: RET):RET




}
