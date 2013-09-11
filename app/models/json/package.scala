package models

import patterns.models._
import play.api.libs.json._

package object json {
  // User
  implicit val userPersistedJsonWriter: Writes[UserPersisted] = UserPersistedSerializer.jsonWriter
  
  // Device
  implicit val devicePersistedJsonWriter: Writes[DevicePersisted] = DevicePersistedSerializer.jsonWriter
  implicit val deviceInfoPersistedJsonWriter: Writes[DeviceInfoPersisted] = DeviceInfoPersistedSerializer.jsonWriter
  implicit val deviceInfoChannelPersistedSerializer: Writes[DevicePersisted] = DeviceInfoChannelPersistedSerializer.jsonWriter
  
  // DeviceType
  implicit val deviceTypePersistedJsonWriter: Writes[DeviceTypePersisted] = DeviceTypePersistedSerializer.jsonWriter

  // DeviceGroup
  implicit val deviceGroupPersistedJsonWriter: Writes[DeviceGroupPersisted] = DeviceGroupPersistedSerializer.jsonWriter
  
  // Simcard
  implicit val simcardPersistedJsonWriter: Writes[SimcardPersisted] = SimcardPersistedSerializer.jsonWriter
  
  // Driver
  implicit val driverPersistedJsonWriter: Writes[DriverPersisted] = DriverPersistedSerializer.jsonWriter
  
  // Vehicle
  implicit val vehiclePersistedJsonWriter: Writes[VehiclePersisted] = VehiclePersistedSerializer.jsonWriter
  
  // VehicleType
  implicit val vehicleTypePersistedJsonWriter: Writes[VehicleTypePersisted] = VehicleTypePersistedSerializer.jsonWriter
  
  // Vehicle Assignements
  implicit val vehicleAssignementPersistedJsonWriter: Writes[VehicleAssignementPersisted] = VehicleAssignementPersistedSerializer.jsonWriter
  
  // Validation Errors
  implicit val validationErrorJsonWriter: Writes[ValidationError] = ValidationErrorSerializer.jsonWriter
  implicit val seqValidationErrorJsonWriter: Writes[Seq[ValidationError]] = SeqValidationErrorSerializer.jsonWriter
  
}