package models

import play.api.libs.json._

package object json {
  implicit val devicePersistedJsonWrtier: Writes[DevicePersisted] = DevicePersistedSerializer.jsonWriter
  implicit val deviceInfoPersistedJsonWrtier: Writes[DeviceInfoPersisted] = DeviceInfoPersistedSerializer.jsonWriter
  implicit val validationErrorJsonWrtier: Writes[ValidationError] = ValidationErrorSerializer.jsonWriter
  implicit val seqValidationErrorJsonWriter: Writes[Seq[ValidationError]] = SeqValidationErrorSerializer.jsonWriter
}