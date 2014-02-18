package org.denigma.genes.mock

import org.openrdf.repository.Repository
import org.openrdf.repository.sail.SailRepository
import org.openrdf.sail.memory.MemoryStore
import org.openrdf.model.vocabulary.{RDFS, OWL}
import org.denigma.genes.workers.GeneDataCenter


/** *
  * Class to test in complex how gene graph and how lookups work
  */
trait MockGeneDataCenter extends GeneDataCenter
{

  override def connect() =
  {
    val repo:Repository = new SailRepository(new MemoryStore())
    repo.initialize()
    val con = repo.getConnection

    val vf = repo.getValueFactory
    con.begin()
    val pr = "http://denigma.de"
    val cl = s"$pr/someclass"
    for(i1<-0 until 10)
    {

      val g1 = vf.createURI(s"$pr/gene"+i1)
      con.add(g1,RDFS.MEMBER,vf.createLiteral(cl))

      for(i2<-0 until 10)
      {
        val n2 = +i1*10+i2
        val g2 = vf.createURI(s"$pr/gene"+n2)
        con.add(g2,RDFS.MEMBER,vf.createLiteral(cl))

        val pre = vf.createURI(s"$pr/interacts")
        con.add(g1,pre,g2)


        for(i3<-0 until 10)
        {
          val n3 = +i1*100+i2*10+i3
          val g3 = vf.createURI(s"$pr/gene"+n3)
          con.add(g3,RDFS.MEMBER,vf.createLiteral(cl))

          con.add(g2,pre,g3)
        }
      }

    }
    con.commit()
    repo.getConnection
  }
}
