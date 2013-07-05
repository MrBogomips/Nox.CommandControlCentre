package utils

import scala.language.implicitConversions
import scala.slick.lifted.MappedTypeMapper

import java.sql.Timestamp
import org.joda.time.DateTime

object Converter {
  implicit def optionStringToBoolean(v: Option[String]): Boolean = v.fold(false){v => v == "on" || v == "true"}
  implicit def jodaDateTimeToTimestamp(v: DateTime): Timestamp = new Timestamp(v.getMillis())
  
  implicit val jodaDateTimeToTimestampMapper = MappedTypeMapper.base[DateTime, Timestamp](v => new Timestamp(v.getMillis()), v => new DateTime(v))
}