package org.denigma.semantic.actors.readers


import akka.actor.Actor
import org.denigma.semantic.actors.NamedActor
import org.denigma.semantic.actors.readers.protocols.ShapeRead
import org.denigma.semantic.reading._
import org.openrdf.model.{Resource, URI}
import org.openrdf.query.TupleQueryResult
import org.scalax.semweb.rdf.Res
import org.scalax.semweb.sesame._
import org.scalax.semweb.sesame.shapes.ShapeReader
import org.scalax.semweb.shex._

import scala.util.{Success, Try}

trait ShExReader {
  me:NamedActor with CanReadBigData with ShapeReader with SimpleReader=>


  /**
   * reads pattern info for catcher
   * @return
   */
  def shapeRead:Actor.Receive = {

    case ShapeRead.LoadProperties(shape,res,contexts)=>
      val props: Try[PropertyModel] = this.loadPropertiesByShape(shape,res)(contexts.map(c=>c:Resource))
      sender ! props


    case ShapeRead.LoadShape(sh,cont)=>
      val shape: Try[Shape] = this.loadShape(sh)(cont.map(r=>r:Resource))
      sender ! shape


    case ShapeRead.LoadShapesForType(iri,context) =>
      val shapes: Try[List[Shape]] = this.loadShapesForType(iri:URI)(context.map(c=>c:Resource)) map(_.toList)
      sender ! shapes

    case ShapeRead.SelectWithShape(query,shape,offset,limit,rewrite)=>
      val res: Try[TupleQueryResult] = this.qsm.select(query.stringValue,offset,limit,rewrite)

      val props: Try[List[PropertyModel]] = this.qsm.select(query.stringValue,offset,limit,rewrite).map{
        case qr=>
          val res = qr.toSelectResults
          res.rows.map{r=>
            val resource = r.collectFirst{case (key,res:Res)=>res}.get
            val p = this.loadPropertiesByShape(shape,resource)
            if(p.isFailure) this.log.error(s"failure loading property mode for resource ${resource.stringValue} and shape ${shape.toString}")
            p
          } collect{ case Success(p)=>p}
      }

      sender ! props


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