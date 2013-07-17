package models

import play.api.libs.json._

package object json {
  implicit val devicePersistedJsonWriter: Writes[DevicePersisted] = DevicePersistedSerializer.jsonWriter
  implicit val deviceInfoPersistedJsonWriter: Writes[DeviceInfoPersisted] = DeviceInfoPersistedSerializer.jsonWriter
  implicit val deviceInfoChannelPersistedSerializer: Writes[DevicePersisted] = DeviceInfoChannelPersistedSerializer.jsonWriter
  
  implicit val validationErrorJsonWriter: Writes[ValidationError] = ValidationErrorSerializer.jsonWriter
  implicit val seqValidationErrorJsonWriter: Writes[Seq[ValidationError]] = SeqValidationErrorSerializer.jsonWriter
  implicit val simcardPersistedJsonWriter: Writes[SimcardPersisted] = SimcardPersistedSerializer.jsonWriter
}