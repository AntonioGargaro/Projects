package controllers

import java.io.{File, FileInputStream, IOException}

import javax.inject._
import models.{SchemaAction, SchemaActionMsg}
import play.api.libs.Files
import play.api.mvc._
import play.api.libs.json._


class schemaController @Inject()(cc: ControllerComponents) (implicit assetsFinder: AssetsFinder)
  extends AbstractController(cc){

  def uploadFile(SCHEMAID: String, request: Request[Files.TemporaryFile]) = {
    /* Saves request body to temporary file */
    val file = new File(s"tmp/$SCHEMAID.json")
    request.body.moveTo(file, replace = true)
  }

  /* This action takes a function as an argument */
  def uploadSchema(SCHEMAID: String) = Action(parse.temporaryFile) { request =>
    if (request.hasBody) {
      try {
        uploadFile(SCHEMAID, request)
      } catch {
        case ioe: IOException => InternalServerError(schemaActionResponse("uploadSchema", SCHEMAID, "fail", "couldn't upload file"))
        case e: Exception => InternalServerError(schemaActionResponse("uploadSchema", SCHEMAID, "fail", "something went wrong"))
      }

      Created(schemaActionResponse("uploadSchema", SCHEMAID, "success"))
    } else
      /* If POST request without body then missing content */
      PartialContent( schemaActionResponse("uploadSchema", SCHEMAID, "fail", "request's body missing") )
  }


  /* Displays the content of the
   * schema file if it exists */
  def downloadSchema(SCHEMAID: String) = Action {
    val fileToServe = new File(s"tmp/$SCHEMAID.json")

    if (fileToServe.exists()) {
      val stream = new FileInputStream(fileToServe)
      val json = try {  Json.parse(stream) } finally { stream.close() }

      Ok(Json prettyPrint(json))
    } else
      BadRequest( schemaActionResponse("downloadSchema", SCHEMAID, "fail", "file does not exist") );
  }

  def validate(SCHEMAID: String) = Action(parse.temporaryFile) { request =>
    val schema = new File(s"tmp/$SCHEMAID.json")
    if (schema.exists()) {
      try {
        uploadFile(SCHEMAID + "-data", request)
      } catch {
        case ioe: IOException => InternalServerError(schemaActionResponse("uploadSchema", SCHEMAID, "fail", "couldn't upload file"))
        case e: Exception => InternalServerError(schemaActionResponse("uploadSchema", SCHEMAID, "fail", "something went wrong"))
      }

      Ok("STILL TO IMPLEMENT")
    } else
      BadRequest( schemaActionResponse("downloadSchema", SCHEMAID, "fail", "schema file does not exist") );
  }

  /* Used for action response to remove code clutter above */
  def schemaActionResponse(action: String, SCHEMAID: String, status: String, message: String = "") : JsValue = {
    if(message == "") Json.toJson(SchemaAction(action, SCHEMAID, status))
    else Json.toJson(SchemaActionMsg(action, SCHEMAID, status, message))
  }
}


