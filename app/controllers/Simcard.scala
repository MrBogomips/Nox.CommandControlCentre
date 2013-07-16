package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models.{ Simcards, Simcard => SimcardModel, SimcardPersisted }
import models.json._
import org.joda.time.format.ISODateTimeFormat
import models.ValidationException

object Simcard extends Secured {

  def index(all: Boolean = false) = WithAuthentication { (user, request) ⇒
    implicit val req = request
    val simcards = all match {
      case false => Simcards.find(Some(true))
      case true => Simcards.find(None)
    }
    if (acceptsJson(request)) {
      Ok(Json.toJson(simcards))
    } else if (acceptsHtml(request)) {
      Ok(views.html.aria.simcard.index(simcards, user))
    } else {
      BadRequest
    }
  }

  def get(id: Int) = WithAuthentication { (user, request) ⇒
    implicit val req = request
    Simcards.findById(id).map { s =>
      if (acceptsJson(request)) {
        Ok(Json.toJson(s))
        //} else if (acceptsHtml(request)) {
        //  OK("get")
        //Ok(views.html.aria.device.item(d.id, user))
      } else {
        BadRequest
      }
    }.getOrElse(NotFound)
  }

  val createForm = Form(
    tuple(
      "imei" -> text,
      "displayName" -> optional(text),
      "description" -> optional(text),
      "mobileNumber" -> text,
      "carrierId" -> number,
      "enabled" -> boolean))

  val updateForm = Form(
    tuple(
      "imei" -> text,
      "displayName" -> optional(text),
      "description" -> optional(text),
      "enabled" -> boolean,
      "mobileNumber" -> text,
      "carrierId" -> number,
      "version" -> number))

  def create = WithAuthentication { implicit request =>
    createForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (imei, displayName0, description, mobileNumber, carrierId, enabled) =>
          val sc = SimcardModel(imei, displayName0, description, enabled, mobileNumber, carrierId)
          val id = Simcards.insert(sc)
          Ok(s"""{"id"=id}""")
      })
  }

  def update(id: Int) = WithAuthentication { implicit request =>
    updateForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (imei, displayName0, description, enabled, mobileNumber, carrierId, version) =>
          val sp = new SimcardPersisted(id, imei, displayName0, description, enabled, mobileNumber, carrierId, version)
          //Simcards.up
          Simcards.update(sp) match {
            case true => Ok(s"Simcard $id updated successfully")
            case _ => NotFound
          }
      })
  }

  def delete(id: Int) = WithAuthentication {
    Simcards.deleteById(id) match {
      case true ⇒ Ok(s"Simcard $id deleted successfully")
      case _ ⇒ NotFound
    }
  }
}