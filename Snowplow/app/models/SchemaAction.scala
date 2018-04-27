
package models

import play.api.libs.json._

object SchemaAction {
  implicit val schemaActionWrites = Json.writes[SchemaAction]
}

case class SchemaAction(action: String, SCHEMAID: String, status: String)