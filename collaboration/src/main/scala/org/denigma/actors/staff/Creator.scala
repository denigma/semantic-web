package org.denigma.actors.staff

import scala.reflect.ClassTag
import akka.actor._
import akka.testkit.TestActorRef

/**
 * This trait is a trait of actor that can fire events and create another actors
 */
trait CreativeActor extends EventActor with Creator{

  /**
   * Another test actor constructor
   * @param name name of the actor to be constructed
   * @param system actor system involved
   * @tparam TA type of the actor
   * @return return the actor constructed
   */
  def createTestActor[TA<:NamedActor:ClassTag](name:String)(system:ActorSystem) =  TestActorRef[TA](Props[TA],self,name)(system)

  def createTestTuple[TA<:NamedActor:ClassTag](name:String)(system:ActorSystem):(TestActorRef[TA],TA) =  TestActorRef[TA](Props[TA],self,name)(system)  match
  {
    case actor:TestActorRef[TA]=>(actor,actor.underlyingActor)
    case _=>
      log.error(s"cannot create test actor with name $name")
      (null, null.asInstanceOf[TA])
  }



}

/**
 * Trait that lets you create actors
 */
trait  Creator{

  def factory: ActorRefFactory

  def createActor[TA<:NamedActor: ClassTag](name:String): ActorRef = factory.actorOf(Props[TA],name)

  def createActor[TA<:NamedActor](actorName:String,creator: ()=>TA):ActorRef = this.factory.actorOf(Props(creator()), name=actorName)

  /**
   * Crates test actors
   * @param name name of test actor
   * @param creator factory function that creates it
   * @param system actor system that is used for that
   * @tparam TA type of testactor
   * @return
   */
  def createTestActor[TA<:NamedActor:ClassTag](name:String,creator: ()=>TA)(system:ActorSystem)=  TestActorRef(creator(),name)(system)




}
