package models.json

import play.api.libs.json._
import org.joda.time.format.ISODateTimeFormat

import models.UserPersisted

object UserPersistedSerializer {
  /**
    * JSON serializer
    */
  implicit val jsonWriter = new Writes[UserPersisted] {
    def writes(u: UserPersisted): JsValue = {
      Json.obj(
        "id" -> u.id,
        "login" -> u.login,
        "displayName" -> u.displayName,
        "status" -> u.status.toString,
        "suspensionReason" -> u.suspensionReason.toString,
        "creationTime" -> ISODateTimeFormat.dateTime.print(u.creationTime.getTime()),
        "modificationTime" -> ISODateTimeFormat.dateTime.print(u.modificationTime.getTime()),
        "version" -> u.version)
    }
  }
}

