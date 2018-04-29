
package models

import play.api.libs.json._

/* Writes arguments to Json format implicitly */
object SchemaAction {
  implicit val schemaActionWrites = Json.writes[SchemaAction]
}

/* Writes arguments to Json format implicitly */
object SchemaActionMsg {
  implicit val schemaActionWritesMsg = Json.writes[SchemaActionMsg]
}

/* Return schema action's status */
case class SchemaAction(action: String, id: String, status: String)
/* Return schema action's status with message */
case class SchemaActionMsg(action: String, id: String, status: String, message: String)
