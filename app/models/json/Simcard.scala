package models.json

import play.api.libs.json._
import org.joda.time.format.ISODateTimeFormat

import models.{ SimcardPersisted }

object SimcardPersistedSerializer {
  /**
    * JSON serializer
    */
  implicit val jsonWriter: Writes[SimcardPersisted] = new Writes[SimcardPersisted] {
    def writes(d: SimcardPersisted): JsValue = {
      Json.obj(
        "id" -> d.id,
        "imei" -> d.imei,
        "displayName" -> d.displayName,
        "description" -> d.description,
        "enabled" -> d.enabled,
        "mobileNumber" -> d.mobileNumber,
        "carrierId" -> d.carrierId,
        "creationTime" -> ISODateTimeFormat.dateTime.print(d.creationTime.getTime()),
        "modificationTime" -> ISODateTimeFormat.dateTime.print(d.modificationTime.getTime()),
        "version" -> d.version,
        "validationErrors" -> d.validationErrors)
    }
  }
}

