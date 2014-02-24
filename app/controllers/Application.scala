package controllers

import play.api.mvc._
import java.io.File
import org.openrdf.rio.RDFFormat
import play.api.libs.json.{JsObject, Json}
import play.api.Play
import play.api.Play.current
import org.denigma.semantic.platform.WithSemanticPlatform


object Application extends PJaxPlatformWith("") with WithSemanticPlatform
{
  def lifespan= Action {
    implicit request=>
      Ok(views.html.lifespan.lifespan())
  }

  def index(controller:String,action:String) = Action {
    implicit request=>
      Ok(views.html.index(controller,action,views.html.main()))

  }


  def menu(root:String) =  Action {
    implicit request=>
      import Json._
      val mns = (1 to 5).map{case i=>
        Json.obj("uri"->s"http://webintelligence.eu/pages/menu$i", "label"->s"menu $i","page"->s"http://webintelligence.eu/pages/$i")
      }.toList
      val menu = Json.obj("menus"->Json.toJson(mns))
      Ok(menu).as("application/json")
  }

  def page(uri:String) =  Action {
    implicit request=>

      Ok(views.html.pages.page("!!!!!!!!!!!!!!!!!!!!!"))
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

    val wi = "http://webintelligence.eu/uploaded"

      if(fname.contains(".ttl"))
        if(sp.db.write{
          con=>
            con.add(file,wi,RDFFormat.TURTLE)
        })
          obj
        else {
          play.Logger.error(fname+" is "+"wrong TURTLE file")
          obj + ("error"->toJson("wrong TURTLE file"))
        }
      else if(fname.contains(".rdf") || fname.contains(".owl"))
        if(sp.db.write{con=>  con.add(file,wi,RDFFormat.RDFXML)})
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