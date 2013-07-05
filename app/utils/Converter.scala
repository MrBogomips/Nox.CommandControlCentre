package utils

import scala.language.implicitConversions

object Converter {
  implicit def optionStringToBoolean(v: Option[String]) = v.fold(false){v => v == "on" || v == "true"}
}