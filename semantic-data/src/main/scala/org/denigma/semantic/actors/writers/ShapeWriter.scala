package org.denigma.semantic.actors.writers

import akka.actor.Actor
import org.denigma.semantic.actors.NamedActor
import org.denigma.semantic.writing.Updater
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.sesame._
import org.scalax.semweb.sesame.shapes.ShapeReader

trait ShapeWriter extends WatchedWriter with  Updater /*with ShapeReader*/{
  self:NamedActor=>


  def updateShape:Actor.Receive = {

    case Update.AddShape(shape,cont)=>
      this.watchedWrite(watcher(shape.toString,lg)){con=>
        val cs = if(cont.isEmpty) List(IRI(WI.RESOURCE)) else cont
        for{
          c <- cs
        } shape.asQuads(c) foreach {
          q=>  con.add(q.sub,q.pred,q.obj,q.cont)
        }

      }

    case Update.UpdateShape(shape,cont)=>
      this.watchedWrite(watcher(shape.toString,lg)){con=>
        val cs = if(cont.isEmpty) List(IRI(WI.RESOURCE)) else cont
        for{
          c <- cs
        } shape.asQuads(c) foreach {
          q=>  con.add(q.sub,q.pred,q.obj,q.cont)
        }

      }
  }

}
