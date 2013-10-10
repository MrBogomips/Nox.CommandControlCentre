package models.json

import play.api.libs.json._
import org.joda.time.format.ISODateTimeFormat

import models.NamedEntityPersisted

trait NamedEntityPersistedSerializer[A <: NamedEntityPersisted[_]] {
  
  /**
    * JSON serializer
    */
  implicit val jsonWriter = new Writes[A] {
    def writes(d: A): JsValue = {
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

/// DeviceType
import models.DeviceTypePersisted
object DeviceTypePersistedSerializer extends NamedEntityPersistedSerializer[DeviceTypePersisted]

/// DeviceGroup
import models.DeviceGroupPersisted
object DeviceGroupPersistedSerializer extends NamedEntityPersistedSerializer[DeviceGroupPersisted]

/// MaintenanceActivityOutcomePersisted
import models.MaintenanceActivityOutcomePersisted
object MaintenanceActivityOutcomePersistedSerializer extends NamedEntityPersistedSerializer[MaintenanceActivityOutcomePersisted]