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


object Application extends PJaxController("")
{
  def lifespan= Action {
    implicit request=>
      Ok(views.html.lifespan.lifespan())
  }

  def index(controller:String,action:String) = Action {
    implicit request=>
      Ok(views.html.index(controller,action,views.html.main()))

  }

  /*
   TODO: improve upload code
    */
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
}