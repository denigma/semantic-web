package org.denigma.genes.workers

import org.denigma.data.{Lookup, SesameConfig}
import org.denigma.genes.models.{GeneInteraction, Gene}
import java.io.File
import org.openrdf.repository.{RepositoryConnection, Repository}
import org.openrdf.repository.sail.SailRepository
import org.openrdf.sail.nativerdf.NativeStore
import org.openrdf.model.Statement
import scalax.collection.mutable.{Graph => MGraph}
import scalax.collection.edge._

import scalax.collection.mutable._
import scalax.collection.edge.Implicits._


/**
 *
 */
trait GeneLoader extends SesameConfig{

  /**
   * Lookup that saves aliases inside itself
   */
  val lookup = new Lookup()

  /**
   * Graph of genes
   */
  val graph:Graph[Gene,LkDiEdge] = Graph.empty[Gene,LkDiEdge]


  def addStatement(value: Statement)  =
  {
    val sub = value.getSubject.stringValue()
    val pre = value.getPredicate.stringValue()
    val obj = value.getObject.stringValue()

    if(pre.contains("alias")) {
      this.lookup.add(sub,obj)
    }
    else
    if(sub.contains("gene") && obj.contains("gene"))
    {
      val g1 = Gene(sub)
      val g2 = Gene(obj)
      val int = GeneInteraction(pre)

      val edge = (g1~+#>g2)(int)
      this.graph.add(edge)

    }
    else
    {
      this.lookup.add(sub,sub)
    }
  }


  /**
   *
   * @return path to sesame repository
   */
  def path = this.db


  /**
   * Connect to sesame repository
   * @return  connteciton
   */
  def connect():RepositoryConnection =
  {
    val dataDir:File = new File(path)
    val repo:Repository = new SailRepository(new NativeStore(dataDir))
    repo.initialize()
    repo.getConnection

  }

  /**
   * adds data to memory (graph + lookup) from sesame connection
   * @param con
   */
  def load(con:RepositoryConnection):Unit =
  {
    val results = con.getStatements(null, null, null, true)
    while(results.hasNext()) this.addStatement(results.next())
    con.close()
  }

  def load() :Unit = this.load(this.connect())
}
