package org.denigma.actors.mock

import org.denigma.actors.{MainActor, Member}
import akka.testkit.TestActorRef
import akka.actor.{ActorSystem, ActorRef}

import scala.reflect.ClassTag
import org.denigma.actors.rooms.{ChatRoomActor, RoomActor}
import org.denigma.actors.workers.AuthWorker


class MockMaster extends MockMain[Member]

/**
 * Test actors that inherits from mock
 */
class MockMain[T<:Member:ClassTag] extends MainActor[T] {


  var testMembers = Map.empty[String,TestActorRef[T]]

  var defTestRoom: TestActorRef[ChatRoomActor] = null

  var authWorkerActor:AuthWorker = null

  override def initAuth() = {
    val actor =  this.createTestActor[AuthWorker]("auth")(this.context.system)
    this.authWorker= actor
    this.authWorkerActor = actor.underlyingActor
    authWorker
  }


  override def preStart() = {
    this.initAuth()
    this.defTestRoom = this.addTestRoom[ChatRoomActor]("all")
    this.defaultRoom = this.rooms.head
    log.debug(s"$name started")
  }


  /* creates TestActorRefs instead of real once
   * @param username
   * @return
   */
  override def addMember(username:String):ActorRef =
  {
    val user = this.createTestActor[T](username)(context.system)
    members+=username->user
    testMembers+=username->user
    this.onAddMember(user)
    user
  }
  override def addRoom[T<:RoomActor:ClassTag](roomName:String): ActorRef ={
    this.addTestRoom[T](roomName)
  }

}
