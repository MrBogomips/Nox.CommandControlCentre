package models.json

import play.api.libs.json._

import models.ValidationError

object ValidationErrorSerializer {
  /**
    * JSON serializer
    */
  implicit val jsonWriter: Writes[ValidationError] = new Writes[ValidationError] {
    def writes(d: ValidationError): JsValue = Json.obj(d.key -> d.conditions)
  }
}

object SeqValidationErrorSerializer {
  /**
    * JSON serializer
    */
  implicit val jsonWriter: Writes[Seq[ValidationError]] = new Writes[Seq[ValidationError]] {
    def writes(errors: Seq[ValidationError]): JsValue = Json.toJson(
      errors.groupBy(_.key).mapValues { errors =>
        errors.flatMap(e => e.conditions)
      }
    )
  }
}