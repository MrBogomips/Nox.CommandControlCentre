package models.json

import play.api.libs.json._
import org.joda.time.format.ISODateTimeFormat

import models.DeviceTypePersisted

object DeviceTypePersistedSerializer {
  /**
    * JSON serializer
    */
  implicit val jsonWriter = new Writes[DeviceTypePersisted] {
    def writes(d: DeviceTypePersisted): JsValue = {
      Json.obj(
        "id" -> d.id,
        "name" -> d.name,
        "display_name" -> d.displayName,
        "description" -> d.description,
        "enabled" -> d.enabled,
        "creation_time" -> ISODateTimeFormat.dateTime.print(d.creationTime.getTime()),
        "modification_time" -> ISODateTimeFormat.dateTime.print(d.modificationTime.getTime()))
    }
  }
}

