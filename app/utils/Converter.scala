package utils

import scala.language.implicitConversions
import scala.slick.lifted.MappedTypeMapper
import java.sql.Timestamp
import org.joda.time.DateTime
import patterns.models.ValidationError
import play.api.libs.json.Writes
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.Results.BadRequest

object Converter {
  implicit def optionStringToBoolean(v: Option[String]): Boolean = v.fold(false){v => v == "on" || v == "true"}
  
  implicit def jodaDateTimeToTimestamp(v: DateTime): Timestamp = new Timestamp(v.getMillis())
  
  //implicit val jodaDateTimeToTimestampMapper = MappedTypeMapper.base[DateTime, Timestamp](v => new Timestamp(v.getMillis()), v => new DateTime(v))

  implicit val validationErrorJsonWriter = new Writes[ValidationError] {
    def writes(e: ValidationError): JsValue = {
      Json.obj{
        e.key -> Json.toJson(e.conditions)
      }
    }
  }    
  implicit val validationErrorsJsonWriter = new Writes[Seq[ValidationError]] {
    def writes(va: Seq[ValidationError]): JsValue = Json.toJson(va)
  }
}