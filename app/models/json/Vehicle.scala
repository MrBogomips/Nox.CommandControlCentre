package models.json

import play.api.libs.json._
import org.joda.time.format.ISODateTimeFormat

import models.{VehiclePersisted, VehicleInfoPersisted}

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
        "vehicleTypeId" -> r.vehicleTypeId,
        "creationTime" -> ISODateTimeFormat.dateTime.print(r.creationTime.getTime()),
        "modificationTime" -> ISODateTimeFormat.dateTime.print(r.modificationTime.getTime()),
        "version" -> r.version)
    }
  }
}

object VehicleInfoPersistedSerializer {
  /**
    * JSON serializer
    */
  implicit val jsonWriter = new Writes[VehicleInfoPersisted] {
    def writes(r: VehicleInfoPersisted): JsValue = {
      Json.obj(
        "id" -> r.id,
        "name" -> r.name,
        "displayName" -> r.displayName,
        "description" -> r.description,
        "enabled" -> r.enabled,
        "model" -> r.model,
        "licensePlate" -> r.licensePlate,
        "vehicleTypeId" -> r.vehicleTypeId,
        "vehicleTypeDisplayName" -> r.vehicleTypeDisplayName,
        "creationTime" -> ISODateTimeFormat.dateTime.print(r.creationTime.getTime()),
        "modificationTime" -> ISODateTimeFormat.dateTime.print(r.modificationTime.getTime()),
        "version" -> r.version)
    }
  }
}
