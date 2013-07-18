package models.json

import play.api.libs.json._
import org.joda.time.format.ISODateTimeFormat

import models.DriverPersisted

object DriverPersistedSerializer {
  /**
    * JSON serializer
    */
  implicit val jsonWriter = new Writes[DriverPersisted] {
    def writes(d: DriverPersisted): JsValue = {
      Json.obj(
        "id" -> d.id,
        "name" -> d.name,
        "surname" -> d.surname,
        "display_name" -> d.displayName,
        "enabled" -> d.enabled,
        "creation_time" -> ISODateTimeFormat.dateTime.print(d.creationTime.getTime()),
        "modification_time" -> ISODateTimeFormat.dateTime.print(d.modificationTime.getTime()))
    }
  }
}

