package models.json

import play.api.libs.json._
import org.joda.time.format.ISODateTimeFormat

import models.VehicleAssignementPersisted

import utils.Converter.jodaDateTimeToTimestamp

object VehicleAssignementPersistedSerializer {
  /**
    * JSON serializer
    */
  implicit val jsonWriter = new Writes[VehicleAssignementPersisted] {
    def writes(va: VehicleAssignementPersisted): JsValue = {
      Json.obj(
        "id" -> va.id,
        "vehicleId" -> va.vehicleId,
        "driverId" -> va.driverId,
        "beginAssignement" -> ISODateTimeFormat.date.print(va.beginAssignement.getTime()),
        "endAssignement" -> ISODateTimeFormat.date.print(va.endAssignement.getTime()),
        "enabled" -> va.enabled,
        "creationTime" -> ISODateTimeFormat.dateTime.print(va.creationTime.getTime()),
        "modificationTime" -> ISODateTimeFormat.dateTime.print(va.modificationTime.getTime()))
    }
  }
}

