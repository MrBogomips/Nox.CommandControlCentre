package models.json

import play.api.libs.json._
import org.joda.time.format.ISODateTimeFormat

import models.{ MaintenanceDutyPersisted, MaintenanceDutyInfoPersisted }

object MaintenanceDutyPersistedSerializer {
  /**
    * JSON serializer
    */
  implicit val jsonWriter: Writes[MaintenanceDutyPersisted] = new Writes[MaintenanceDutyPersisted] {
    def writes(m: MaintenanceDutyPersisted): JsValue = {
      Json.obj(
        "id" -> m.id,
        "idVehicle" -> m.idVehicle,
        "idService" -> m.idService,
        "creationTime" -> ISODateTimeFormat.dateTime.print(m.creationTime.getTime()),
        "modificationTime" -> ISODateTimeFormat.dateTime.print(m.modificationTime.getTime()),
        "version" -> m.version,
        "validationErrors" -> m.validationErrors)
    }
  }
}

object MaintenanceDutyInfoPersistedSerializer {
  /**
    * JSON serializer
    */
  implicit val jsonWriter: Writes[MaintenanceDutyInfoPersisted] = new Writes[MaintenanceDutyInfoPersisted] {
    def writes(m: MaintenanceDutyInfoPersisted): JsValue = {
      Json.obj(
        "id" -> m.id,
        "idVehicle" -> m.idVehicle,
        "idService" -> m.idService,
        "creationTime" -> ISODateTimeFormat.dateTime.print(m.creationTime.getTime()),
        "modificationTime" -> ISODateTimeFormat.dateTime.print(m.modificationTime.getTime()),
        "version" -> m.version,
        "validationErrors" -> m.validationErrors,
        "serviceName" -> m.serviceName,
        "serviceDisplayName" -> m.serviceDisplayName,
        "vehicleName" -> m.vehicleName,
        "vehicleDisplayName" -> m.vehicleDisplayName,
        "vehicleLicensePlate" -> m.vehicleLicensePlate
      )
    }
  }
}