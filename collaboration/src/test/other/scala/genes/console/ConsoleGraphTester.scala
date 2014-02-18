package org.denigma.genes.console

import scala.Predef._
import scalax.collection.mutable.{Graph => MGraph}
import scalax.collection.GraphEdge._
import scalax.collection.edge._

import scalax.collection.mutable._
import scalax.collection.edge.Implicits._

import scalax.collection.io.json.descriptor.predefined._
import scalax.collection.io.json._
import scalax.collection.io.json.exp._
case class Person(val name:String)
case class Relationship(val kind:String, val strength: Int = 1)

object Son extends Relationship("son")

object ConsoleGraphTester extends App
{

  def generate():Graph[Person,LkDiEdge] = {
    val g: Graph[Person,LkDiEdge] = Graph.empty[Person,LkDiEdge]
    val p = Person("root")

    val friend = "friend"
    val enemy = "enemy"
    val neutral = "neutral"
    val en = Relationship(enemy,3)
    val fr = Relationship(friend,2)
    val neu = Relationship(neutral)


    for(i<-1 until 10)
    {
      val fr1 = Person(friend+"_1_#"+i)
      val en1 = Person(enemy+"_1_#"+i)
      val neu1 = Person(neutral+"_1_#"+i)
      /*
      * for all g.add function calls I get "cannot resolve" red highlight but they all compile and run well despite this warning
      * this wrong highligting appeared only in 0.7.264 version of Scala plugin
      * before that it worked fine
      * */
      g.add((p~+#>fr1)(fr))
      g.add((p~+#>neu1)(neu))
      g.add((p~+#>en1)(en))

      for(j<-1 until 10)
      {
        val fr2 = Person(friend+"_2_#"+i*10+j)
        val en2 = Person(enemy+"_2_#"+i*10+j)
        val neu2 = Person(neutral+"_2_#"+i*10+j)
        val v: LkDiEdge[Person] with EdgeCopy[LkDiEdge] {type L1 = Relationship} = (fr1~+#>fr2)(fr)
        g.add((fr1~+#>fr2)(fr))
        g.add((neu1~+#>neu2)(neu))
        g.add((en1~+#>en2)(en))

      }
    }
    return g
  }

  val g:Graph[Person,LkDiEdge] = this.generate()



  val personDesc: descriptor.NodeDescriptor[Person] {def id(node: Any): String} = new NodeDescriptor[Person](typeId = "Persons") {
    def id(node: Any) = node match {
      case Person(name) => name
    }
  }
  val desc: Descriptor[Person] = new Descriptor[Person](personDesc, LkDi.descriptor(Relationship("")))


  //another way to export
  //val expLGraph= g.toJson(quickJson)    //here IDEA cannot find implicit toJson method so I have to use another way to export


  val export = new Export[Person,LkDiEdge](g, desc)

  import export._
  val (nodesToExport, edgesToExport) = (jsonASTNodes, jsonASTEdges)
  val astToExport = jsonAST(nodesToExport ++ edgesToExport)
  //println(jsonText(jsonAST(edgesToExport)))
  println(jsonText(astToExport))

  println("export finished")
  println("============================================")

  val js: String = jsonText(astToExport)
  val jgComp = new JsonGraphCoreCompanion[Graph](Graph)
  val jsonGraph = jgComp.fromJson[Person,LkDiEdge](js,desc)
  /*
  Exception in thread "main" net.liftweb.json.MappingException: unknown error
  Caused by: java.lang.IllegalAccessException:
  Class net.liftweb.json.Extraction$ can not access a member of class org.denigma.semantic.graph.Relationship$ with modifiers "private"
  */
  println(jsonGraph.toString)

  println("test import finished")
  println("============================================")



}