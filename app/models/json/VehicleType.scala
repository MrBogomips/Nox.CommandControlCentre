package models.json

import play.api.libs.json._
import org.joda.time.format.ISODateTimeFormat

import models.VehicleTypePersisted

object VehicleTypePersistedSerializer {
  /**
    * JSON serializer
    */
  implicit val jsonWriter = new Writes[VehicleTypePersisted] {
    def writes(d: VehicleTypePersisted): JsValue = {
      Json.obj(
        "id" -> d.id,
        "name" -> d.name,
        "displayName" -> d.displayName,
        "description" -> d.description,
        "enabled" -> d.enabled,
        "creationTime" -> ISODateTimeFormat.dateTime.print(d.creationTime.getTime()),
        "modificationTime" -> ISODateTimeFormat.dateTime.print(d.modificationTime.getTime()),
        "version" -> d.version)
    }
  }
}

