package controllers

import java.io.{File, FileInputStream, IOException}

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.github.fge.jsonschema.main.{JsonSchema, JsonSchemaFactory}
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

  def uploadSchema(SCHEMAID: String) = Action(parse.temporaryFile) { request =>
    if (request.hasBody) {

      /* Upload SCHEMA file */
      try { uploadFile(SCHEMAID, request) }
      catch {
        case ioe: IOException => InternalServerError(schemaActionResponse("uploadSchema", SCHEMAID, "fail", "Couldn't upload file"))
        case e: Exception => InternalServerError(schemaActionResponse("uploadSchema", SCHEMAID, "fail", "Something went wrong"))
      }

      val schemaFile = new File(s"tmp/$SCHEMAID.json")

      if (schemaFile.exists()) {
        val stream = new FileInputStream(schemaFile)
        try {
          Json.parse(stream)
          Created(schemaActionResponse("uploadSchema", SCHEMAID, "success"))
        }
        catch {
          case e: JsonParseException => {
            /* Close stream and delete file */
            stream.close() ; schemaFile.delete()
            InternalServerError(schemaActionResponse("uploadSchema", SCHEMAID, "fail", "Invalid Json"))
          }
        } finally { stream.close() }

      } else
        BadRequest( schemaActionResponse("uploadSchema", SCHEMAID, "fail", "Error uploading file") );
    } else
      /* If POST request without body then missing content */
      PartialContent( schemaActionResponse("uploadSchema", SCHEMAID, "fail", "Request's body missing") )
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
      BadRequest( schemaActionResponse("downloadSchema", SCHEMAID, "fail", s"${SCHEMAID}.json does not exist") );
  }

  def validate(SCHEMAID: String) = Action(parse.temporaryFile) { request =>
    val schema = new File(s"tmp/$SCHEMAID.json")
    if (schema.exists()) {
      try {
        uploadFile(SCHEMAID + "-data", request)
      } catch {
        case ioe: IOException => InternalServerError(schemaActionResponse("uploadSchema", SCHEMAID, "fail", "Couldn't upload file"))
        case e: Exception => InternalServerError(schemaActionResponse("uploadSchema", SCHEMAID, "fail", "Something went wrong"))
      }

      val dataFile = new File(s"tmp/$SCHEMAID-data.json")
      val datastream = new FileInputStream(dataFile)
      val schemastream = new FileInputStream(schema)

      val mapper: ObjectMapper = new ObjectMapper()

      val jsonSchema: JsonNode = try { mapper.readTree(Json.parse(schemastream).toString()) } finally { schemastream.close }

      val jsonData: JsValue = Json.parse(datastream)
      val jsonData2: JsObject = JsObject(jsonData.as[JsObject].fields.filterNot(k => withoutValue(k._2)))
      println(jsonData2)

      val jsonDataNode: JsonNode = try { mapper.readTree(jsonData2.toString()) } finally { datastream.close }


      val factory: JsonSchemaFactory = JsonSchemaFactory.byDefault()

      val schema1: JsonSchema = factory.getJsonSchema(jsonSchema)

      //println(schema1.validate(jsonDataNode))

      Ok("STILL TO IMPLEMENT")
    } else
      BadRequest( schemaActionResponse("downloadSchema", SCHEMAID, "fail", s"${SCHEMAID}.json does not exist") );
  }

  def withoutValue(v: Any):Boolean = {
    println(v.getClass) ;
    v match {
      case JsNull => true
      case JsString("") => true
      case _ => false

    }
  }

  /* Used for action response to remove code clutter above */
  def schemaActionResponse(action: String, SCHEMAID: String, status: String, message: String = "") : JsValue = {
    if(message == "") Json.toJson(SchemaAction(action, SCHEMAID, status))
    else Json.toJson(SchemaActionMsg(action, SCHEMAID, status, message))
  }
}


