package controllers.literature

import org.scalax.semweb.rdf.vocabulary._
import org.scalax.semweb.rdf.{IRI, vocabulary}
import org.scalax.semweb.shex.{ExactlyOne, Shape, ShapeBuilder, Star}


trait ArticleShapes{

  val de = IRI("http://denigma.org/resource/")
  val rep = new ShapeBuilder(de / "Research_Support")


  val pmid = IRI("http://denigma.org/resource/Pubmed/")
  val dc = IRI(vocabulary.DCElements.namespace)

  val article = de /"Article"
  val authors =     de / "is_authored_by"
  val abs = de / "abstract"
  val published = de / "is_published_in"
  val title = de / "title"
  val excerpt = de / "excerpt"

  val desc = (WI.PLATFORM / "has_description").iri
  val completed = (WI.PLATFORM / "is_completed").iri
  val assigned = (WI.PLATFORM / "assigned_to").iri
  val task = (WI.PLATFORM / "Task").iri
  val Anton = de / "Anton_Kulaga"
  val priority = (WI.PLATFORM / "has_priority").iri

  val articleShapeRes = de / "Article_Shape"

  private def makeArticleShape = {
    val art = new ShapeBuilder(articleShapeRes)
    art has de /"is_authored_by" occurs Star //occurs Plus
    art has de / "is_published_in" occurs Star //occurs Plus
    art has dc / "title" occurs Star //occurs ExactlyOne
    //art has de / "date" occurs Star //occurs ExactlyOne
    art has de / "abstract" of XSD.StringDatatypeIRI  occurs Star//occurs Star
    art has  de / "excerpt" of XSD.StringDatatypeIRI  occurs Star//occurs Star
    art
  }

  val articleShape: Shape = this.makeArticleShape.result

  private def makeTaskShape = {
    //val shapeRes = new IRI("http://shape.org")

    val ts = new ShapeBuilder(task)
    ts has de /"is_authored_by" occurs Star //occurs Plus
    ts has title occurs Star //occurs ExactlyOne
    //art has de / "date" occurs Star //occurs ExactlyOne
    ts has desc of XSD.StringDatatypeIRI  occurs Star//occurs Star
    ts has assigned occurs Star //occurs Plus
    ts has priority occurs ExactlyOne
    ts has completed of XSD.BooleanDatatypeIRI  occurs Star//occurs Star
    ts
  }


  val taskShape: Shape = this.makeTaskShape.result




}