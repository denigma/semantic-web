package controllers.literature
/*

import java.util.Date

import auth.UserAction
import controllers.{PJaxPlatformWith, PickleController}
import org.denigma.binding.messages.ExploreMessages
import org.denigma.binding.messages.ExploreMessages._
import org.denigma.binding.messages.ModelMessages.ModelMessage
import org.denigma.binding.picklers.rp
import org.denigma.binding.play.AjaxExploreEndpoint
import org.denigma.semantic.controllers.ShapeController
import org.denigma.semantic.reading.SelectResult
import org.scalajs.spickling.playjson._
import org.scalax.semweb.rdf.vocabulary._
import org.scalax.semweb.rdf._
import org.scalax.semweb.shex.{PropertyModel, Shape}
import org.scalax.semweb.sparql
import org.scalax.semweb.sparql._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.Result

import scala.concurrent.Future
import scala.util.{Failure, Success}



trait ExplorePapers extends PickleController with ShapeController with ArticleShapes with AjaxExploreEndpoint{
  self:PJaxPlatformWith=>

  var shapes:Map[Res,Shape] = Map( IRI("http://denigma.org/resource/article")->articleShape,
    IRI("http://webintelligence.eu/queries/tasks")->this.taskShape)

  private val papersQuery: sparql.SelectQuery =     SELECT(?("s")) WHERE Pat(?("s"), RDF.TYPE, this.article)

  private val tasksQuery: sparql.SelectQuery =     SELECT(?("s")) WHERE Pat(?("s"), RDF.TYPE, this.task)


  //data-param-shape="http://denigma.org/resource/article"
  //data-param-query="http://denigma.org/query/sens"

  val queries: Map[Res,org.scalax.semweb.sparql.SelectQuery] = Map(IRI("http://denigma.org/query/sens")->papersQuery,
    IRI("http://webintelligence.eu/queries/tasks") -> this.tasksQuery)



  override type ExploreRequest = auth.AuthRequest[ExploreMessage]

  override type ExploreResult = Future[Result]



  def badQuery(q:String):PartialFunction[Throwable,Result] = {
    case e=>
      val er = e.getMessage
      play.Logger.info(s"Query failed \n $q \n with the following error $er")
      Ok(SelectResult.badRequest(q,er)).as("application/sparql-results+json")
  }

  protected def withQueryShape(query:Res,shape:Res)(fun:(sparql.SelectQuery,Shape)=> Future[Result]): Future[Result] =
  {
    this.shapes.get(shape) match {
      case Some(sh) =>
        this.queries.get(query) match {
          case Some(q) =>
            fun(q,sh)
          case None => Future.successful(BadRequest(Json.obj("status" -> "KO", "message" -> "no query found")).as("application/json"))

        }
      case None =>
        Future.successful(BadRequest(Json.obj("status" -> "KO", "message" -> "no shape found")).as("application/json"))
    }
  }

  protected def exploreItems(items:List[PropertyModel],exploreMessage:ExploreMessages.Explore): List[PropertyModel] = {
    val list: List[PropertyModel] = items.filter{case a=>
    val res = exploreMessage.filters.forall(_.matches(a))
    res
    }

    exploreMessage.sortOrder match {
    case Nil=>list
    case s::xs=>
    play.Logger.debug("sort takes place")

    list.sortWith{case (a,b)=>s.sort(xs)(a,b) > -1}
    }

  }

  override def onExplore(exploreMessage: Explore)(implicit request: ExploreRequest): ExploreResult =  this.withQueryShape(exploreMessage.query,exploreMessage.shape){ (q,sh)=>
      this.selectWithShape(q,sh) map {
        case Success(res: List[PropertyModel])=>

          val exp = ExploreMessages.Exploration(this.articleShape,res,exploreMessage)

          val p = rp.pickle(exp)
          play.Logger.debug(s"SIZE + ${exp.models.size}")
          //play.Logger.debug(p2.toString())
          Ok(p).as("application/json")


        case Failure(th)=>
          play.Logger.error(s"error in sparql query $q \n"+th.getMessage.toString)
          this.badQuery(q.stringValue)(th)

      }

    }

  override def onSelect(selectMessage: ExploreMessages.SelectQuery)(implicit request: ExploreRequest): ExploreResult =
    this.withQueryShape(selectMessage.query,selectMessage.shapeId){ (q,sh)=>
    {
      this.selectWithShape(q,sh) map {
        case Success(res)=>
          val p = rp.pickle(res)
          Ok(p).as("application/json")

        case Failure(th)=>
          play.Logger.error(s"error in sparql query $q \n"+th.getMessage.toString)
          this.badQuery(q.stringValue)(th)

      }

    }
    }

  override def onBadExploreMessage(message: ModelMessage)(implicit request: ExploreRequest): ExploreResult = {
    Future.successful(BadRequest(Json.obj("status" ->"KO","message"->"bad explore message")).as("application/json"))

  }

  override def onExploreSuggest(suggestMessage: ExploreSuggest)(implicit request: ExploreRequest): ExploreResult =
    this.withQueryShape(suggestMessage.explore.query,suggestMessage.explore.shape){ (q,sh)=>
    {
      this.selectWithShape(q,sh) map {
        case Success(res)=>
          val result = res
            .collect { case item if item.properties.contains(suggestMessage.prop) =>
            item.properties(suggestMessage.prop).collect {
              case p if p.stringValue.contains(suggestMessage.typed) => p
            }
          }.flatten

          val mes = ExploreMessages.ExploreSuggestion(suggestMessage.typed, result.toList, suggestMessage.id, suggestMessage.channel, new Date())
          val p = rp.pickle(mes)
          Ok(p).as("application/json")

        case Failure(th)=>
          play.Logger.error(s"error in sparql query $q \n"+th.getMessage.toString)
          this.badQuery(q.stringValue)(th)

      }

    }
    }

  def exploreEndpoint() = UserAction.async(this.pickle[ExploreMessage]()){implicit request=>
    this.onExploreMessage(request.body)(request)

  }



}*/
