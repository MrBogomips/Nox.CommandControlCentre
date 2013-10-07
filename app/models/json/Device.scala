package models.json

import play.api.libs.json._
import org.joda.time.format.ISODateTimeFormat

import models.{ DevicePersisted, DeviceInfoPersisted }

object DevicePersistedSerializer {
  /**
    * JSON serializer
    */
  implicit val jsonWriter: Writes[DevicePersisted] = new Writes[DevicePersisted] {
    def writes(d: DevicePersisted): JsValue = {
      Json.obj(
        "id" -> d.id,
        "name" -> d.name,
        "displayName" -> d.displayName,
        "description" -> d.description,
        "enabled" -> d.enabled,
        "simcardId" -> d.simcardId,
        "deviceTypeId" -> d.deviceTypeId,
        "deviceGroupId" -> d.deviceGroupId,
        "deviceManagerId" -> d.deviceManagerId,
        "vehicleId" -> d.vehicleId,
        "creationTime" -> ISODateTimeFormat.dateTime.print(d.creationTime.getTime()),
        "modificationTime" -> ISODateTimeFormat.dateTime.print(d.modificationTime.getTime()),
        "version" -> d.version,
        "validationErrors" -> d.validationErrors)
    }
  }
}

object DeviceInfoPersistedSerializer {
  /**
    * JSON serializer
    */
  implicit val jsonWriter: Writes[DeviceInfoPersisted] = new Writes[DeviceInfoPersisted] {
    def writes(d: DeviceInfoPersisted): JsValue = {
      Json.obj(
        "id" -> d.id,
        "name" -> d.name,
        "displayName" -> d.displayName,
        "description" -> d.description,
        "enabled" -> d.enabled,
        "simcardId" -> d.simcardId,        
        "deviceTypeId" -> d.deviceTypeId,
        "deviceGroupId" -> d.deviceGroupId,
        "deviceManagerId" -> d.deviceManagerId,
        "deviceManagerName" -> d.deviceManagerName,
        "deviceManagerDisplayName" -> d.deviceManagerDisplayName,
        "vehicleId" -> d.vehicleId,
        "deviceTypeDisplayName" -> d.deviceTypeDisplayName,
        "deviceGroupDisplayName" -> d.deviceGroupDisplayName,
        "vehicleDisplayName" -> d.vehicleDisplayName,
        "vehicleLicensePlate" -> d.vehicleLicensePlate,
        "simcardImei" -> d.simcardImei,
        "simcardDisplayName" -> d.simcardDisplayName,
        "creationTime" -> ISODateTimeFormat.dateTime.print(d.creationTime.getTime()),
        "modificationTime" -> ISODateTimeFormat.dateTime.print(d.modificationTime.getTime()),
        "version" -> d.version,
        "validationErrors" -> d.validationErrors)
    }
  }
}

object DeviceInfoChannelPersistedSerializer {
  /**
    * JSON serializer
    */
  implicit val jsonWriter: Writes[DevicePersisted] = new Writes[DevicePersisted] {
    def writes(d: DevicePersisted): JsValue = {
      Json.obj(
         "id" -> d.id,
        "name" -> d.name,
        "displayName" -> d.displayName,
        "description" -> d.description,
        "enabled" -> d.enabled,
        "simcardId" -> d.simcardId,
        "deviceTypeId" -> d.deviceTypeId,
        "deviceGroupId" -> d.deviceGroupId,
        "deviceManagerId" -> d.deviceManagerId,
        "vehicleId" -> d.vehicleId,
        "creationTime" -> ISODateTimeFormat.dateTime.print(d.creationTime.getTime()),
        "modificationTime" -> ISODateTimeFormat.dateTime.print(d.modificationTime.getTime()),
        "version" -> d.version,
        "channels" -> List("NOXT1", "NOXT2"),
        "validationErrors" -> d.validationErrors)
    }
  }
}