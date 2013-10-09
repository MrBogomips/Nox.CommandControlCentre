package models.json

import play.api.libs.json._
import org.joda.time.format.ISODateTimeFormat

import models.{ MaintenanceServicePersisted }

object MaintenanceServicePersistedSerializer {
  /**
    * JSON serializer
    */
  implicit val jsonWriter: Writes[MaintenanceServicePersisted] = new Writes[MaintenanceServicePersisted] {
    def writes(m: MaintenanceServicePersisted): JsValue = {
      Json.obj(
        "id" -> m.id,
        "name" -> m.name,
        "displayName" -> m.displayName,
        "description" -> m.description,
        "enabled" -> m.enabled,
        "odometer" -> m.odometer,
        "monthsPeriod" -> m.monthsPeriod,
        "creationTime" -> ISODateTimeFormat.dateTime.print(m.creationTime.getTime()),
        "modificationTime" -> ISODateTimeFormat.dateTime.print(m.modificationTime.getTime()),
        "version" -> m.version,
        "validationErrors" -> m.validationErrors)
    }
  }
}