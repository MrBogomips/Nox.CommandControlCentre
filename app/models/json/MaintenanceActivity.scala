package models.json

import play.api.libs.json._
import org.joda.time.format.ISODateTimeFormat

import models.{ MaintenanceActivityPersisted }

object MaintenanceActivityPersistedSerializer {
  /**
    * JSON serializer
    */
  implicit val jsonWriter: Writes[MaintenanceActivityPersisted] = new Writes[MaintenanceActivityPersisted] {
    def writes(m: MaintenanceActivityPersisted): JsValue = {
      Json.obj(
        "id" -> m.id,
        "idVehicle" -> m.idVehicle,
        "idOperator" -> m.idOperator,
        "odometer" -> m.odometer,
        "note" -> m.note,
        "odometer" -> m.odometer,
        "activityStart" -> ISODateTimeFormat.dateTime.print(m.activityStart),
        "activityEnd" -> ISODateTimeFormat.dateTime.print(m.activityEnd),
        "modificationTime" -> ISODateTimeFormat.dateTime.print(m.modificationTime.getTime()),
        "version" -> m.version,
        "validationErrors" -> m.validationErrors)
    }
  }
}