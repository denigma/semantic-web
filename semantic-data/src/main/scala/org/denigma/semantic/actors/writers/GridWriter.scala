package org.denigma.semantic.actors.writers

import akka.actor.Actor
import org.denigma.semantic.actors.NamedActor
import org.denigma.semantic.writing.Updater
import org.openrdf.model.Statement
import org.openrdf.model.impl.StatementImpl
import org.scalax.semweb.rdf.{Trip, IRI}
import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.sesame.shapes.ShapeReader
import org.scalax.semweb.shex.PropertyModel
import org.scalax.semweb.sesame._

/**
 * Writer for adding data
 */
trait GridWriter extends WatchedWriter with  Updater with ShapeWriter
{
  self:NamedActor=>

  def updateGrid:Actor.Receive = {

    case  Update.AddModels(models,shapeOpt, contexts) =>
      this.watchedWrite(watcher(shapeOpt.toString+"_add_"+models.hashCode.toString, lg)) { con =>
        for{
          m <- models
          (p,values) <- m.properties
          v <- values
        } con.add(m.id,p,v)
      }
  }

}
