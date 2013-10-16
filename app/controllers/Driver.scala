package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models.{ Driver => DriverModel, DriverPersisted, Drivers }

import org.joda.time.format.ISODateTimeFormat

import models.json.driverPersistedJsonWriter

object Driver extends Secured {
  def index(all: Boolean = false) = WithAuthentication { (user, request) =>
    implicit val req = request
    val drivers = all match {
      case false => Drivers.find(Some(true))
      case true  => Drivers.find(None)
    }
    if (acceptsJson(request)) {
      Ok(Json.toJson(drivers))
    } else if (acceptsHtml(request)) {
      Ok(views.html.aria.driver.index(drivers, user))
    } else {
      BadRequest
    }
  }

  def get(id: Int) = WithAuthentication { (user, request) =>
    Drivers.findById(id).map { d ⇒
      if (acceptsJson(request)) {
        Ok(Json.toJson(d))
      } else if (acceptsHtml(request)) {
        Ok(views.html.aria.driver.item(d.id, user))
      } else {
        BadRequest
      }
    }.getOrElse(NotFound);
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

  def create = WithAuthentication { implicit request =>
    createForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, surname, displayName, enabled) =>
          val dt = DriverModel(name, surname, displayName, enabled)
          val id = Drivers.insert(dt)
          Ok(s"""{"id"=id}""")
      })
  }

  def update(id: Int) = WithAuthentication { implicit request =>
    updateForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, surname, displayName, enabled, version) =>
          val d = DriverPersisted(id, name, surname, displayName, enabled, version = version)
          //Simcards.up
          Drivers.update(d) match {
            case true => Ok
            case _    => NotFound
          }
      })
  }

  def delete(id: Int) = WithAuthentication {
    Drivers.deleteById(id) match {
      case true => Ok
      case _    => NotFound
    }
  }
}