package models.json

import play.api.libs.json._
import org.joda.time.format.ISODateTimeFormat

import models.VehiclePersisted

object VehiclePersistedSerializer {
  /**
    * JSON serializer
    */
  implicit val jsonWriter = new Writes[VehiclePersisted] {
    def writes(r: VehiclePersisted): JsValue = {
      Json.obj(
        "id" -> r.id,
        "name" -> r.name,
        "display_name" -> r.displayName,
        "description" -> r.description,
        "enabled" -> r.enabled,
        "model" -> r.model,
        "license_plate" -> r.licensePlate,
        "creation_time" -> ISODateTimeFormat.dateTime.print(r.creationTime.getTime()),
        "modification_time" -> ISODateTimeFormat.dateTime.print(r.modificationTime.getTime()))
    }
  }
}

