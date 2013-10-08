package models.json

import play.api.libs.json._
import org.joda.time.format.ISODateTimeFormat

import models.OperatorPersisted

object OperatorPersistedSerializer {
  /**
    * JSON serializer
    */
  implicit val jsonWriter = new Writes[OperatorPersisted] {
    def writes(d: OperatorPersisted): JsValue = {
      Json.obj(
        "id" -> d.id,
        "name" -> d.name,
        "surname" -> d.surname,
        "displayName" -> d.displayName,
        "enabled" -> d.enabled,
        "creationTime" -> ISODateTimeFormat.dateTime.print(d.creationTime.getTime()),
        "modificationTime" -> ISODateTimeFormat.dateTime.print(d.modificationTime.getTime()),
        "version" -> d.version)
    }
  }
}

