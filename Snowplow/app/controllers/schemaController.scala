package controllers

import javax.inject._

import models.SchemaAction
import play.api.mvc._
import play.api.libs.json._

class schemaController @Inject()(cc: ControllerComponents) (implicit assetsFinder: AssetsFinder)
  extends AbstractController(cc){
  def upload(SCHEMAID: String) = Action {
    Ok(Json.obj("result" -> SchemaAction("uploadSchema", SCHEMAID, "success")))
  }
}