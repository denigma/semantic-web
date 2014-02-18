package org.denigma.genes.workers

import org.denigma.data.SesameConfig
import org.denigma.genes.models.{FishEye, Gene}
import scalax.collection.mutable._
import org.denigma.genes.messages._


import scalax.collection.GraphTraversal._

import org.denigma.genes.messages.GeneTraverse
import scalax.collection.edge._
import org.denigma.genes.staff.{GraphVisitor, UniqueNodeVisitor}

/**
 * This trait that works with genes data.
 * It is used extensively by GeneWorker that inherits from it
 * it inherits geneloader that is capable of loading the data from Sesame database
 */
trait GeneDataCenter  extends GeneLoader with SesameConfig
{

  /**
   * Config variable defining the mode in which GeneDataCenter operates by default
   * if it is strict it searches in genes only with strict equality
   */
  val strictMode = false

  /**
   * Defines the default value wether to use aliases of genes in search or not
   */
  val aliasMode = true


  def geneById(name:String)  = this.graph.find(Gene(name))


  def geneNodeById(geneName:String)  = this.graph.nodes.find(n=>n.value match {
    case Gene(name)=>name==geneName
    case _=>false
  }
  )

  /**
   * Returns gene outer node by its name
   * @param name name of a gene
   * @param strict should it be strict or you can give a part of gene's name
   * @param searchAlias should it use alias in search
   * @return
   */
  def geneNodeByName(name:String,strict:Boolean = this.strictMode, searchAlias:Boolean = this.aliasMode) =
    if(searchAlias)
    {
      this.lookup.dictionary.keys.find(k=>k==name)  match
      {
        case Some(name:String)=> this.geneNodeById(name)
        case None=>
          this.lookup.suggestKeysByValues(name) match
          {
            case collection if collection.size == 0=>None
            case collection if strict && collection.head._1!=name =>None
            case collection if strict && collection.head._1==name =>this.geneNodeById(name)
            case collection => this.geneNodeById(collection.head._1)
          }

      }
    }else this.geneById(name)

  /**
   * Returns path between two genes
   * @param fromGene name of the gene from where we start
   * @param toGene name of the gene with which we end
   * @param strict should the gene name be equal
   * @param searchAlias search in gene aliases
   * @return
   */
  def getPath(fromGene:String,toGene:String,strict:Boolean = this.strictMode, searchAlias:Boolean = this.aliasMode):Option[this.graph.Path] =
  {

     (this.geneNodeByName(fromGene,strict,searchAlias) , this.geneNodeByName(toGene,strict,searchAlias)) match
     {
       case (Some(nFrom),Some(nTo))=> nFrom.pathTo(nTo)
       case (_,_)=>None
     }
 }

  /**
   * traverses the graph in specified direction
   * @param name name of the gene
   * @param depth maxDepth of traversal
   * @param direction direction of traversal
   * @param strict if is true than only exact name will be used, if no parts of the name are ok
   * @param searchAlias if true makes a search inside aliases
   * @return option with nodes
   */
  def traverseNodes(name:String,depth:Int = 1,direction:Direction = AnyConnected,
               strict:Boolean = this.strictMode, searchAlias:Boolean = this.aliasMode):Option[Set[this.graph.NodeT]] =
    this.geneNodeByName(name, strict,searchAlias) match
    {
      case None=> None
      case Some(node)=>
        val vis = new UniqueNodeVisitor[graph.NodeT]
        node.traverseNodes(maxDepth = depth,direction = direction,nodeFilter =vis.nodeFilter)(vis.add)
        Some(vis.nodes)
    }

  /**
   * Makes traversal with params from GeneTraversal message
   * @param tra GeneTraversal message
   * @return Graph of traversed Genes
   */
  def makeNodesTraversal(tra:GeneTraverse)= this.traverseNodes(tra.name,tra.depth,tra.direction,tra.strict,tra.searchAlias)


  /**
   * traverses the graph in specified direction and returs a graph value
   * @param name name of the gene
   * @param depth maxDepth of traversal
   * @param direction direction of traversal
   * @param strict if is true than only exact name will be used, if no parts of the name are ok
   * @param searchAlias if true makes a search inside aliases
   * @return option with nodes
   */
  def traverseGraph(name:String,depth:Int = 1,direction:Direction = AnyConnected,
               strict:Boolean = this.strictMode, searchAlias:Boolean = this.aliasMode): Graph[Gene, LkDiEdge]
  =
    this.geneNodeByName(name, strict,searchAlias) match
    {
      case None=> Graph.empty[Gene,LkDiEdge]
      case Some(node:graph.NodeT)=>
        //val tr = node.traverse(maxDepth = depth,direction = direction)_
        val tr = node.traverseEdges(maxDepth = depth,direction = direction)_
        val vis = new GraphVisitor[Gene,LkDiEdge]
        //tr(vis.addNode,vis.addEdge)
        tr(vis.addEdge)


        vis.graph
    }

  /**
   * Traverses the graph and writes the results to another graph
   * @param tra GeneTraversal object
   */
  def makeGraphTraversal(tra:GeneTraverse): Graph[Gene, LkDiEdge] = this.traverseGraph(tra.name,tra.depth,tra.direction,tra.strict,tra.searchAlias)

  /**
   * Make a traversal based on data from FishEye
   * @param fy FishEye message
   * @return
   */
  def traverseFishEye(fy:FishEye) = GraphChange(fy.graph,this.makeGraphTraversal(GeneTraverse.fromFishEye(fy)),fy.mode)



}