package org.denigma.semantic.actors.writers

import akka.actor.Actor
import org.denigma.semantic.actors.NamedActor
import org.denigma.semantic.writing.Updater
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.sesame._
import org.scalax.semweb.sesame.shapes.ShapeReader

trait ShapeWriter extends WatchedWriter with  Updater with ShapeReader{
  self:NamedActor=>


  def UpdateShape:Actor.Receive = {

    case Update.AddShape(shape,cont)=>
      this.watchedWrite(watcher(shape.toString,lg)){con=>

        shape.asQuads(cont.getOrElse(IRI(WI.RESOURCE))) foreach {
          q=>  con.add(q.sub,q.pred,q.obj,q.cont)
        }

      }

    case Update.UpdateShape(shape,cont)=>
      this.watchedWrite(watcher(shape.toString,lg)){con=>
        //con.remove(shape.label,cont)

        //TODO: write proper removal

        shape.asQuads(cont.getOrElse(IRI(WI.RESOURCE))) foreach {
          q=>  con.add(q.sub,q.pred,q.obj,q.cont)
        }

      }
  }

}
