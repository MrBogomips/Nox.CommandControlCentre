package models.json

import play.api.libs.json._
import org.joda.time.format.ISODateTimeFormat

import models.NamedEntityPersisted

trait NamedEntityPersistedSerializer[PERSISTED <: NamedEntityPersisted[_]] {

  /**
    * JSON serializer
    */
  implicit val jsonWriter = new Writes[PERSISTED] {
    private def instanceToJsonObj(d: PERSISTED) = Json.obj(
      "id" -> d.id,
      "name" -> d.name,
      "displayName" -> d.displayName,
      "description" -> d.description,
      "enabled" -> d.enabled,
      "creationTime" -> ISODateTimeFormat.dateTime.print(d.creationTime.getTime()),
      "modificationTime" -> ISODateTimeFormat.dateTime.print(d.modificationTime.getTime()),
      "version" -> d.version)

    def writes(d: PERSISTED): JsValue = instanceToJsonObj (d)
    def writesSeq(d: Seq[PERSISTED]): JsValue = {
      Json.arr(
        d.map {writes(_)}
      )(0)
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
