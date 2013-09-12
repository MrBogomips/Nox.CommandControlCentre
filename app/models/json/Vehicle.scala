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
        "displayName" -> r.displayName,
        "description" -> r.description,
        "enabled" -> r.enabled,
        "model" -> r.model,
        "licensePlate" -> r.licensePlate,
        "creationTime" -> ISODateTimeFormat.dateTime.print(r.creationTime.getTime()),
        "modificationTime" -> ISODateTimeFormat.dateTime.print(r.modificationTime.getTime()),
        "version" -> r.version)
    }
  }
}

