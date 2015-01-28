package org.denigma.semantic.actors.readers


import akka.actor.Actor
import org.denigma.semantic.actors.NamedActor
import org.denigma.semantic.actors.readers.protocols.{StringShapeRead, ShapeRead}
import org.denigma.semantic.reading._
import org.openrdf.model.{Statement, Resource, URI}
import org.openrdf.query.TupleQueryResult
import org.openrdf.repository.RepositoryResult
import org.scalax.semweb.messages.Results.SelectResults
import org.scalax.semweb.rdf.vocabulary.RDF
import org.scalax.semweb.rdf.{RDFValue, Res}
import org.scalax.semweb.sesame._
import org.scalax.semweb.sesame.shapes.ShapeReader
import org.scalax.semweb.shex._

import scala.util.{Success, Try}

trait ShExReader {
  me:NamedActor with CanReadBigData with ShapeReader with SimpleReader=>

  def loadQueryWithShape(query:String, shape:Shape, offset:Long,limit:Long,rewrite:Boolean): Try[Set[PropertyModel]] =
  {
    this.qsm.select(query,offset,limit,rewrite).flatMap{
      case qr=>
        val res: SelectResults = qr.toSelectResults
        val rows: List[Map[String, RDFValue]] = res.rows
        val resources = rows.map(r=>r.collectFirst{case (key,res:Res)=>res}.get:Resource).toSet
        val models = this.loadPropertyModelsByShape(shape,resources)
        models
    }
  }



    /**
   * reads pattern info for catcher
   * @return
   */
  def shapeRead:Actor.Receive = {

    case ShapeRead.LoadArc(res,cts) =>
        val arc = this.read{ case con=>
          this.extractor.getArc(res:Resource,con)(cts.map(r => r:Resource)).get
        }
        sender ! arc

    case ShapeRead.LoadAllShapes(cts) =>
      val shapes= this.loadAllShapes(cts.map(c=>c:Resource))
      sender ! shapes

    case ShapeRead.LoadPropertyModels(shape,resources,contexts)=>
      val props =    this.loadPropertyModelsByShape(shape,resources.map(r=>r:Resource))(contexts.map(c=>c:Resource))
      sender ! props


    case ShapeRead.LoadShape(sh,cont)=>
      val shape: Try[Shape] = this.loadShape(sh)(cont.map(r=>r:Resource))
      sender ! shape


    case ShapeRead.LoadShapes(resources,cont)=>
      val shapes =  this.read { con=>resources.map(sh=>extractor.getShape(sh,con)(cont.map(c=>c:Resource))).toList}
        //this.loadShapes(resources:_*)(cont.map(r=>r:Resource))
      sender ! shapes

    case ShapeRead.LoadShapesForType(iri,cont) =>
      val shapes: Try[List[Shape]] = this.loadShapesForType(iri:URI)(cont.map(c=>c:Resource)) map(_.toList)
      sender ! shapes

    case ShapeRead.SelectWithShape(query,shape,offset,limit,rewrite)=>

      sender ! this.loadQueryWithShape(query.stringValue,shape,offset,limit,rewrite)

    case ShapeRead.SelectWithShapeRes(query,shapeRes,offset,limit,rewrite) =>

      sender ! this.loadShape(shapeRes).flatMap{
        shape=> this.loadQueryWithShape(query.stringValue,shape,offset,limit,rewrite).map(res=>shape->res)
      }


    case StringShapeRead.SelectWithShapeRes(query,shapeRes,offset,limit,rewrite)=>

      //play.api.Logger.info(s"SHAPED recieved $query with $shapeRes")
      val result: Try[(Shape, Set[PropertyModel])] = this.loadShape(shapeRes).flatMap{
        shape=> this.loadQueryWithShape(query,shape,offset,limit,rewrite).map(res=>shape->res)
      }
      sender ! result


      //    case ShapeRead.SuggestWithShape(typed,prop,shape)=>
      //shape.arcRules().find(r=>r.name)
      //val q = SELECT (?("sug")) WHERE Pat(?("sug"), ?("any"), ?("any"))


//    case ShapeRead.SelectWithShapes(query,shapes,offset,limit,rewrite)=>
//      val q: SelectQuery = query
//      this.qsm.select(query.stringValue,offset,limit,rewrite).map{
//        case qr=>
//          val res = qr.toSelectResults
//          this.loadPropertiesByShape()
//      }
//      sender !res
  }

//  override def loadPropertiesByShape(sh:Shape,res:Resource)(implicit contexts:Seq[Resource] = List.empty[Resource]): Try[PropertyModel] = this.read { con =>
//    sh.rule match {
//      case and: AndRule =>
//        val arcs = and.conjoints.collect { case arc: ArcRule => arc}
//
//        val result = arcs.foldLeft[PropertyModel](PropertyModel.clean(res)) { case (model, arc) =>
//          arc.name match {
//            case NameTerm(prop) =>
//              val (pr: IRI, values: Seq[Value]) = this.propertyByArc(res, prop, arc)(con, contexts)
//              val v = this.checkOccurrence(arc.occurs, pr, values)
//              val vals: Set[RDFValue] = values.map(v => v: RDFValue).toSet
//              model.copy(properties = model.properties + (pr -> vals), validation = model.validation.and(v))
//
//            case _ => model
//          }
//        }
//        result
//
//
//      case r =>
//        lg.warn(s"or rule is not yet supported, passed rule is ${r.toString}")
//        ???
//    }
//  }
//
// override def propertyByArc(res:Res,p:IRI,arc:ArcRule)(implicit con:ReadConnection, contexts:Seq[Resource] = List.empty[Resource]): (IRI, Seq[Value]) =  arc.value match {
//    case ValueSet(s)=>
//      p -> con.objects(res, p).filter(o => s.contains(o: RDFValue))
//
//    case ValueType(i)=>
//      if(i.stringValue.contains(vocabulary.XSD.namespace)) {
//        lg.warn("XSD IS NOT YET CHECKED")
//        p ->con.objects(res,p)
//
//      }
//      else {
//        p -> con.resources(res,p,contexts).filter(o=>con.hasStatement(o,RDF.TYPE,i,true))
//      }
//
//    case _ => ???
//
//  }


}