package controllers

import play.api.mvc._
import java.io.File
import org.openrdf.rio.RDFFormat
import play.api.libs.json.{JsObject, Json}
import play.api.Play
import play.api.Play.current
import org.denigma.semantic.test.WithSemanticPlatform


/*
main application controller, responsible for index and some other core templates and requests
 */
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
   TODO: test upload code
   WARNING: NOT TESTED
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
      val uplCon = "http://webintelligence.eu/uploaded/"
      val pr = this.sp.db.parseFile(file,uplCon)

      if(pr.isFailure) obj + ("error"->toJson("WRONG SEMANTIC WEB FILE"))  else obj

    }.toList
    val res = Json.obj("files" -> Json.toJson(files))
    Ok(res)
  }
}
