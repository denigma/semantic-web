package org.denigma.genes
import scala.Predef._
import org.scalatest._
import org.denigma.genes.models.{FishEye, Gene}
import scalax.collection.GraphTraversal._
import org.denigma.genes.mock.MockGeneDataCenter
import org.denigma.genes.messages.{GraphChange, GeneTraverse}
import scalax.collection.edge.LkDiEdge

/**
 * Test class to test gene traversals
 */
class GeneDataCenterSpec extends WordSpec with Matchers
{
  val genes = new MockGeneDataCenter{}
  genes.load()

 "GeneDataManager" should
 {
   val pref = "http://denigma.de"
   val noname = s"$pref/nonamegene"
   val name =  s"$pref/gene50"
   val fromName =  s"$pref/gene5"
   val toName =  s"$pref/gene500"



   "do lookup" in {
     /*
     * I do now use aliases here but gene==gene
     * */
     //val col = this.genes.lookup.suggestKeysByValues("50")
     val col = this.genes.lookup.suggestValuesByKeys("50")
     col.size.shouldEqual(20)
     col.head._1.shouldEqual("http://denigma.de/gene50")
     val cont:Boolean = col.forall{case (key,value)=>key.contains("50") && value.forall(v=>v.contains("50"))}
     cont.shouldEqual(true)

   }

   "get genes by id" in {

     this.genes.geneById(noname) shouldEqual(None)
     this.genes.geneNodeById(noname) shouldEqual(None)


     this.genes.geneById(name).shouldEqual(Some(Gene(name)))
     this.genes.geneNodeById(name) match
     {
       case None=>this.fail("Should find a gene there")
       case Some(node)=>
         node.value.shouldEqual(Gene(name))
         val inc = node.incoming
         inc.size.shouldEqual(1)
         val out = node.outgoing
         out.size.shouldEqual(10)
       case _=>this.fail("Should find a gene there")
     }


   }

   "get gene nodes by alias" in {

     this.genes.geneNodeByName("50",true) match
     {
       case None=>println("strict mode works well")
       case Some(node)=>this.fail("should not return anything in strict more here")

     }

     this.genes.geneNodeByName("50") match
     {
       case None=>this.fail("Should find a gene there")
       case Some(node)=>
         node.value.shouldEqual(Gene(name))
         val inc = node.incoming
         inc.size.shouldEqual(1)
         val out = node.outgoing
         out.size.shouldEqual(10)
       case _=>this.fail("Should find a gene there")
     }


   }

   "make successors traversal for selected gene" in {

     this.genes.traverseNodes("50",1,direction = Successors) match {
       case None=>this.fail("should return real traversal results")
       case Some(nodes)=>
         nodes.size.shouldEqual(11)
         val list = nodes.map(n=>n.value).toList
         list.contains(Gene(name)).shouldEqual(true)

         list.contains(Gene(s"$pref/gene501")).shouldEqual(true)
         list.contains(Gene(s"$pref/gene51")).shouldEqual(false)
         list.contains(Gene(s"$pref/gene601")).shouldEqual(false)
         list.contains(Gene(noname)).shouldEqual(false)
         list.contains(Gene(s"$pref/gene508")).shouldEqual(true)

     }
   }

   "make AnyConnected traversal for specified gene" in {
     this.genes.traverseNodes("50",1,direction = AnyConnected) match {
       case None=>this.fail("should return real traversal results")
       case Some(nodes)=>
         nodes.size.shouldEqual(12)
         val list = nodes.map(n=>n.value).toList
         list.contains(Gene(name)).shouldEqual(true)
         list.contains(Gene(s"$pref/gene5")).shouldEqual(true)

         list.contains(Gene(s"$pref/gene501")).shouldEqual(true)
         list.contains(Gene(s"$pref/gene51")).shouldEqual(false)
         list.contains(Gene(s"$pref/gene601")).shouldEqual(false)
         list.contains(Gene(noname)).shouldEqual(false)
         list.contains(Gene(s"$pref/gene508")).shouldEqual(true)

     }

   }

   "make graphTraversal with Graph as a result" in {

    val tra = GeneTraverse("50",1,direction = AnyConnected)
    val graph = this.genes.makeGraphTraversal(tra)

     val list = graph.nodes.toList//map(n=>n.).toList

     list.contains(Gene(name)).shouldEqual(true)
     list.contains(Gene(s"$pref/gene5")).shouldEqual(true)

     list.contains(Gene(s"$pref/gene501")).shouldEqual(true)
     list.contains(Gene(s"$pref/gene51")).shouldEqual(false)
     list.contains(Gene(s"$pref/gene601")).shouldEqual(false)
     list.contains(Gene(noname)).shouldEqual(false)
     list.contains(Gene(s"$pref/gene508")).shouldEqual(true)


   }

   "make fisheye Traversal" in {

     val gup: GraphChange[Gene, LkDiEdge] = this.genes.traverseFishEye(new FishEye("genes","50",GeneTraverse.direction2str(Successors),1,"load"))
     val list = gup.graph.nodes.toList
     list.contains(Gene(name)).shouldEqual(true)

     list.contains(Gene(s"$pref/gene501")).shouldEqual(true)
     list.contains(Gene(s"$pref/gene51")).shouldEqual(false)
     list.contains(Gene(s"$pref/gene601")).shouldEqual(false)
     list.contains(Gene(noname)).shouldEqual(false)
     list.contains(Gene(s"$pref/gene508")).shouldEqual(true)

   }

   "get path between two nodes" in {
     this.genes.getPath("5","500") match
     {
       case None=>this.fail("we should get the path here")
       case Some(path)=>
         path.startNode.value.shouldEqual(Gene(fromName))
         path.endNode.value.shouldEqual(Gene(toName))
         val iter = path.nodeIterator
         iter.hasNext.shouldEqual(true)
         iter.next().value.shouldEqual(Gene(fromName))
         iter.hasNext.shouldEqual(true)
         iter.next().value.shouldEqual(Gene(name))
         iter.hasNext.shouldEqual(true)
         iter.next().value.shouldEqual(Gene(toName))
         iter.hasNext.shouldEqual(false)

     }

     this.genes.getPath("5","wrongGene") match
     {
       case None=>println("nopath works as expected")
       case Some(path)=>this.fail("we should get None here")
     }


   }




 }
}
