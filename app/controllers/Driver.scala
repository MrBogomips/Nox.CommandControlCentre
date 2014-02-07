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
  
  lazy val ariaController: String = getThisClassSimpleName
  val pageTitle: String = "Drivers"
  val createButton : String = "driver"
    
  private def getThisClassSimpleName: String = {
    val s = this.getClass.getSimpleName()
    s.substring(0, s.length() - 1)
  }
  
  def index(all: Boolean = false) = WithCors("GET") {
    WithAuthentication { (user, request) =>
      implicit val req = request

      if (acceptsJson(request)) {
        val drivers = all match {
          case false => Drivers.find(Some(true))
          case true  => Drivers.find(None)
        }
        Ok(Json.toJson(drivers))
      } else if (acceptsHtml(request)) {
//        Ok(views.html.aria.driver.index(user))
        Ok(views.html.aria.datatable.index(user,ariaController,pageTitle,createButton))
      } else {
        BadRequest
      }
    }
  }

  def get(id: Int) = WithCors("GET", "PUT", "DELETE") {
    WithAuthentication { (user, request) =>
      Drivers.findById(id).map { d =>
        if (acceptsJson(request)) {
          Ok(Json.toJson(d))
        } else if (acceptsHtml(request)) {
          Ok(views.html.aria.driver.item(d.id, user))
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
            val dt = DriverModel(name, surname, displayName, enabled)
            val id = Drivers.insert(dt)
            Ok(s"""{"id":$id}""").as("application/json")
        })
    }
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