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
import patterns.models.ValidationException

object Simcard extends Secured {

  lazy val ariaController: String = getThisClassSimpleName
  val pageTitle: String = "Simcards"
  val createButton : String = "simcard"
    
  private def getThisClassSimpleName: String = {
    val s = this.getClass.getSimpleName()
    s.substring(0, s.length() - 1)
  }
  
  def index(all: Boolean = false) = WithCors("GET") {
    WithAuthentication { (user, request) =>
      implicit val req = request  
      if (acceptsJson(request)) {
        val simcards = all match {
	      case false => Simcards.find(Some(true))
	      case true  => Simcards.find(None)
	    }
        Ok(Json.toJson(simcards))
      } else if (acceptsHtml(request)) {
//        Ok(views.html.aria.simcard.index(user))
        Ok(views.html.aria.datatable.index(user,ariaController,pageTitle,createButton))
      } else {
        BadRequest
      }
    }
  }

  def get(id: Int) = WithCors("GET", "PUT", "DELETE") {
    WithAuthentication { (user, request) =>
      implicit val req = request
      Simcards.findById(id).map { s =>
        if (acceptsJson(request)) {
          Ok(Json.toJson(s))
        } else {
          BadRequest
        }
      }.getOrElse(NotFound)
    }
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

  def create = WithCors("POST") {
    WithAuthentication { implicit request =>
      createForm.bindFromRequest.fold(
        errors => BadRequest(errors.errorsAsJson).as("application/json"),
        {
          case (imei, displayName0, description, mobileNumber, carrierId, enabled) =>
            val sc = SimcardModel(imei, displayName0, description, enabled, mobileNumber, carrierId)
            val id = Simcards.insert(sc)
            Ok(s"""{"id":$id}""").as("application/json")
        })
    }
  }

  def update(id: Int) = WithAuthentication { implicit request =>
    updateForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (imei, displayName0, description, enabled, mobileNumber, carrierId, version) =>
          val sp = new SimcardPersisted(id, imei, displayName0, description, enabled, mobileNumber, carrierId, version)
          //Simcards.up
          Simcards.update(sp) match {
            case true => Ok
            case _    => NotFound
          }
      })
  }

  def delete(id: Int) = WithAuthentication {
    Simcards.deleteById(id) match {
      case true => Ok
      case _    => NotFound
    }
  }
}