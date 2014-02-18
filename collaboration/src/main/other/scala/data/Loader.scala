package org.denigma.data

import java.io.File
import org.openrdf.repository.Repository
import org.openrdf.repository.sail.SailRepository
import org.openrdf.sail.nativerdf.NativeStore

/**
 * Loads info from RDF repository
 * test class
 */
trait Loader {

  def path =""



  /**this is needed to make it runnable*/
  def load() = {


    val dataDir:File = new File(path)
    val graph:Repository = new SailRepository(new NativeStore(dataDir))
    graph.initialize()



    val con= graph.getConnection

    val results = con.getStatements(null, null, null, true);

    while(results.hasNext())
    {
      val value = results.next()
      println(value.getSubject)
      println(value.getPredicate)
      println(value.getObject)
    }


    con.close()
  }
}
