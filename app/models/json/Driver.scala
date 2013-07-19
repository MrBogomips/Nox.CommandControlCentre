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
        "displayName" -> d.displayName,
        "enabled" -> d.enabled,
        "creationTime" -> ISODateTimeFormat.dateTime.print(d.creationTime.getTime()),
        "modificationTime" -> ISODateTimeFormat.dateTime.print(d.modificationTime.getTime()),
        "version" -> d.version)
    }
  }
}

