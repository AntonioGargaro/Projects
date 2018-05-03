package controllers

import java.io.{File, FileInputStream, IOException}

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.github.fge.jsonschema.core.report.{ProcessingMessage, ProcessingReport}
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
    val schemaFile = new File(s"tmp/$SCHEMAID.json")

    if (schemaFile.exists()) {
      try {
        /* Upload JSON data file */
        uploadFile(SCHEMAID + "-data", request)
      } catch {
        case ioe: IOException => InternalServerError(schemaActionResponse("uploadSchema", SCHEMAID, "fail", "Couldn't upload file"))
        case e: Exception => InternalServerError(schemaActionResponse("uploadSchema", SCHEMAID, "fail", "Something went wrong"))
      }

      /* Read both SCHEMA file and DATA file */
      val dataFile = new File(s"tmp/$SCHEMAID-data.json")
      val dataStream = new FileInputStream(dataFile)
      val schemaStream = new FileInputStream(schemaFile)

      /* Used to make JsonNode for SCHEMA and DATA */
      val mapper: ObjectMapper = new ObjectMapper()

      /* Make nodes from file streams */
      val jsonSchema: JsonNode = try { mapper.readTree(Json.parse(schemaStream).toString()) } finally { schemaStream.close }
      val jsonDataNode: JsonNode = try { mapper.readTree(clean(Json.parse(dataStream)).toString()) } finally { dataStream.close }

      /* Schema validation */
      val factory: JsonSchemaFactory = JsonSchemaFactory.byDefault()
      val schema: JsonSchema = factory.getJsonSchema(jsonSchema)
      val pr: ProcessingReport = schema.validate(jsonDataNode)

      if(pr.isSuccess){
        Ok( schemaActionResponse("validateDocument", SCHEMAID, "success") )
      } else {
        var errormessage: String = ""
        pr.forEach({ message: ProcessingMessage =>
          errormessage = message.getMessage
        })
        BadRequest(schemaActionResponse("validateDocument", SCHEMAID, "fail", errormessage))
      }


    } else
      BadRequest( schemaActionResponse("downloadSchema", SCHEMAID, "fail", s"${SCHEMAID}.json does not exist") );
  }

  /* Recursive remove all nulls */
  def clean(json: JsValue): JsObject = {
    var newObj = Json.obj()
    val it: Iterator[(String, JsValue)] = json.as[JsObject].fields.iterator
    while(it.hasNext) {
      var temp: (String, JsValue) = it.next()
      if (temp._2.asOpt[JsObject] != None){
        temp = (temp._1, clean(temp._2))
      }
      if(!withoutValue(temp._2)){
        newObj = newObj + temp
      }
    }
    newObj
  }

  def withoutValue(v: JsValue):Boolean = {
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


