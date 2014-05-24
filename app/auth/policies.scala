package auth


import org.scalax.semweb.rdf.{Res, IRI}
import org.denigma.semantic.platform.AppConfig

//case object DefaultPolicy extends PolicyCollection
//{
//
//  override val permissions = Set(RootPolicy,GuestPolicy)
//  override val bans = Set.empty
//}

case object GuestPolicy extends Policy{

  override def canCreate(user: IRI, resource: Res): Boolean = false

  override def canRead(user: IRI, resource: Res): Boolean = true

  override def canUpdate(user: IRI, resource: Res): Boolean = false

  override def canDelete(user: IRI, resource: Res): Boolean = false

}

trait PolicyCollection extends Policy{

  val permissions: Set[Policy]

  val bans: Set[Ban]

  //isSigned && this.policies.exists(p=>p.canCreate(username.get,res))
  override def canCreate(currentUser:IRI,res: Res): Boolean = !bans.exists(b=> !b.canCreate(currentUser, res)) && permissions.exists(p=>p.canCreate(currentUser,res))

  override def canRead(currentUser:IRI,res: Res): Boolean = !bans.exists(b=> !b.canRead(currentUser, res)) && permissions.exists(p=>p.canRead(currentUser,res))

  override def canUpdate(currentUser:IRI,res: Res): Boolean = !bans.exists(b=> !b.canUpdate(currentUser, res)) && permissions.exists(p=>p.canUpdate(currentUser,res))

  override def canDelete(currentUser:IRI,res: Res): Boolean = !bans.exists(b=> !b.canDelete(currentUser, res)) && permissions.exists(p=>p.canDelete(currentUser,res))


}


trait Ban extends Policy {
  override def isBanPolicy = true
}

/**
 * Policy
 */
trait Policy {

  def canCreate(user:IRI,resource: Res): Boolean
  def canRead(user:IRI,resource: Res): Boolean
  def canUpdate(user:IRI,resource: Res): Boolean
  def canDelete(user:IRI,resource: Res): Boolean

  def isBanPolicy = false


}
/**
 * Root as god
 */
case object RootPolicy extends Policy  with AppConfig{

  lazy val root = this.currentAppConfig.getString("root").map(i=>IRI(i))


  protected def isRoot(user:IRI) = root.isDefined && root.get ==user

  override def canCreate(user: IRI, resource: Res): Boolean =isRoot(user)

  override def canRead(user: IRI, resource: Res): Boolean = isRoot(user)

  override def canUpdate(user: IRI, resource: Res): Boolean = isRoot(user)

  override def canDelete(user: IRI, resource: Res): Boolean = isRoot(user)
}
