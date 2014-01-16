package controllers

import play.api.mvc._
import org.openrdf.model.impl.URIImpl
import org.openrdf.repository.RepositoryResult
import org.denigma.semantic.data.{LoveHater, SG}
import SG.db
import scala.collection.JavaConversions._
import scala.collection.immutable._
import org.openrdf.model._
import java.io.File
import org.openrdf.rio.RDFFormat
import play.api.libs.json.{JsObject, Json}
import play.api.Play
import play.api.Play.current


object Application extends Controller with LoveHater{

  def lifespan= Action {
    implicit request=>
      Ok(views.html.lifespan.lifespan())
  }

  def index = Action {
    implicit request=>

      val s: URIImpl = new URIImpl("http://www.bigdata.com/rdf#Daniel")

      //      db.write{
      //        implicit con=>
      //          val p =  new URIImpl("http://www.bigdata.com/rdf#loves")
      //          val o = new URIImpl("http://www.bigdata.com/rdf#RDF")
      //          val st = new StatementImpl(s, p, o)
      //          con.add(st)
      //      }
      val res: scala.List[Statement] = db.read{
        implicit r=>
          val iter: RepositoryResult[Statement] = r.getStatements(null,null,null,true)
          iter.asList().toList
      }.getOrElse(List.empty)

      Ok(views.html.index(res))

  }

  val defQ:String="""
           SELECT ?subject ?object WHERE
           {
              ?subject <http://denigma.org/relations/resources/loves> ?object.
           }
           """



  def query(query:String=defQ) = Action {
    implicit request=>
      this.addTestRels()

      val results = SG.db.query(query)

      Ok(views.html.query(results))

  }

  def testUpload = Action { implicit request =>
    val body = request.body
  Ok("wef")
  }

  def upload = Action(parse.multipartFormData) { implicit request =>
    import Json._
    val uploads = Play.getFile("public/uploads/")
    val p = uploads.getAbsolutePath
    val files: scala.List[JsObject] = request.body.files.map{ f=>
      val fname = f.filename

      val name = p+"/"+fname
      val file = new File(name)

      f.ref.moveTo(file,replace = true)


      val obj: JsObject = Json.obj(
        "name"->f.filename,
        "size"->file.length(),
        "url"-> (request.domain+"/uploads/"+f.filename)
      )

      if(fname.contains(".ttl"))
        if(SG.db.write{
          con=>
            con.add(file,null,RDFFormat.TURTLE)
            val a=1
        })
          obj
        else {
          play.Logger.error(fname+" is "+"wrong TURTLE file")
          obj + ("error"->toJson("wrong TURTLE file"))
        }
      else if(fname.contains(".rdf") || fname.contains(".owl"))
        if(SG.db.write{con=>  con.add(file,null,RDFFormat.RDFXML)})
        {
          obj
        }

        else {
          play.Logger.error(fname+" is "+"wrong RDF file")
          obj + ("error"->toJson("wrong RDF file"))
        }
      else {
        play.Logger.error(fname+" is "+"NOT SEMANTIC WEB FILE")
        obj + ("error"->toJson("NOT SEMANTIC WEB FILE"))
      }
    }.toList
    val res = Json.obj("files" -> Json.toJson(files))
    Ok(res)
  }

  /*
  {"files": [
  {
    "name": "picture1.jpg",
    "size": 902604,
    "url": "http:\/\/example.org\/files\/picture1.jpg",
    "thumbnailUrl": "http:\/\/example.org\/files\/thumbnail\/picture1.jpg",
    "deleteUrl": "http:\/\/example.org\/files\/picture1.jpg",
    "deleteType": "DELETE"
  },
  {
    "name": "picture2.jpg",
    "size": 841946,
    "url": "http:\/\/example.org\/files\/picture2.jpg",
    "thumbnailUrl": "http:\/\/example.org\/files\/thumbnail\/picture2.jpg",
    "deleteUrl": "http:\/\/example.org\/files\/picture2.jpg",
    "deleteType": "DELETE"
  }
]}

{"files": [
  {
    "name": "picture1.jpg",
    "size": 902604,
    "error": "Filetype not allowed"
  },
  {
    "name": "picture2.jpg",
    "size": 841946,
    "error": "Filetype not allowed"
  }
]}
   */



}