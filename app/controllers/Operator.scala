package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models.{ Operators, Operator => OperatorModel, OperatorPersisted }
import org.joda.time.format.ISODateTimeFormat
import patterns.models.ValidationException

object Operator extends Secured {

  import models.json.{ operatorPersistedJsonWriter }

  def index2(all: Boolean = false) = WithCors("GET") {
    WithAuthentication { (user, request) =>
      implicit val req = request
      val operators = all match {
        case false => Operators.find(Some(true))
        case true  => Operators.find(None)
      }
      if (acceptsJson(request)) {
        Ok(Json.toJson(operators))
      } else if (acceptsHtml(request)) {
        ??? // Ok(views.html.aria.device.index(devices, user))
      } else {
        BadRequest
      }
    }
  }

  def index(all: Boolean = false) = WithCors("GET") {
    WithAuthentication { (user, request) =>
      implicit val req = request
      if (acceptsJson(request)) {
        val operators = all match {
          case false => Operators.find(Some(true))
          case true  => Operators.find(None)
        }
        Ok(Json.toJson(operators))
      } else if (acceptsHtml(request)) {
        Ok(views.html.aria.operator.index(user))
      } else {
        BadRequest
      }
    }
  }

  def get(id: Int) = WithCors("GET", "PUT", "DELETE") {
    WithAuthentication { (user, request) =>
      implicit val req = request
      Operators.findById(id).map { d =>
        if (acceptsJson(request)) {
          Ok(Json.toJson(d))
        } else if (acceptsHtml(request)) {
          ??? // Ok(views.html.aria.device.item(d.id, user))
        } else {
          BadRequest
        }
      }.getOrElse(NotFound);
    }
  }

  val createForm = Form(
    tuple(
      "name" -> nonEmptyText(minLength = 3),
      "surname" -> nonEmptyText(minLength = 3),
      "displayName" -> optional(text),
      "enabled" -> boolean))
  val updateForm = Form(
    tuple(
      "name" -> nonEmptyText(minLength = 3),
      "surname" -> nonEmptyText(minLength = 3),
      "displayName" -> optional(text),
      "enabled" -> boolean,
      "version" -> number))

  def create = WithCors("POST") {
    WithAuthentication { implicit request =>
      createForm.bindFromRequest.fold(
        errors => BadRequest(errors.errorsAsJson).as("application/json"),
        {
          case (name, surname, displayName, enabled) =>
            val d = OperatorModel(name, surname, displayName, enabled)
            val id = Operators.insert(d)
            Ok(s"""{"id":$id}""").as("application/json")
        })
    }
  }

  def update(id: Int) = WithAuthentication { implicit request =>
    updateForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, surname, displayName, enabled, version) =>
          val dp = new OperatorPersisted(id, name, surname, displayName, enabled, version = version)
          Operators.update(dp) match {
            case true => {
              Ok(s"Operator $id updated successfully")
            }
            case _ => NotFound
          }
      })
  }

  def delete(id: Int) = WithAuthentication {
    Operators.deleteById(id) match {
      case true => {
        Ok(s"Operator $id deleted successfully")
      }
      case _ => NotFound("")
    }
  }
}