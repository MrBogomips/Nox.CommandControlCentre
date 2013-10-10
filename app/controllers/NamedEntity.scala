package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models.{ NamedEntityTrait, NamedEntity => NamedEntityModel, NamedEntityPersisted, NamedEntities }
import models.json.NamedEntityPersistedSerializer
import java.sql.Timestamp
import org.joda.time.format.ISODateTimeFormat

/**
 * Common control to manage NamedEntity objects
 */
trait NamedEntityController[TRAIT <: NamedEntityTrait, MODEL <: NamedEntityModel[TRAIT], PERSISTED <: NamedEntityPersisted[MODEL]] extends Secured {
  //trait NamedEntityController[TRAIT, MODEL , PERSISTED ] extends Secured {
  val ariaController: String // = "devicegroups"
  val pageTitle: String // = "Device Group"
  val playController: String // = "DeviceGroup" // match the object name

  val dataAccessObject: NamedEntities[TRAIT, MODEL, PERSISTED]
  implicit val jsonSerializer: NamedEntityPersistedSerializer[PERSISTED]

  def modelBuilder(name: String, displayName: Option[String], description: Option[String], enabled: Boolean): MODEL

  def persistedBuilder(id: Int, name: String, displayName: Option[String], description: Option[String], enabled: Boolean, version: Int): PERSISTED

  // COMMON METHODS
  def index(all: Boolean = false) = WithAuthentication { (user, request) =>
    implicit val req = request

    val entities: Seq[PERSISTED] = all match {
      case false => dataAccessObject.find(Some(true))
      case true  => dataAccessObject.find(None)
    }
    if (acceptsJson(request)) {
      //Ok(Json.toJson( jsonSerializer.jsonWriter.writes(entities.seq) ))
      //Ok(Json.toJson( entities )(jsonSerializer.jsonWriter))
      //Ok( entities.map(Json.toJson(_)(jsonSerializer.jsonWriter)) )
      import scala.language.reflectiveCalls
      Ok(jsonSerializer.jsonWriter.writesSeq(entities))
    } else if (acceptsHtml(request)) {
      Ok(views.html.aria.namedentity.index(entities, user, ariaController, pageTitle, playController))
    } else {
      BadRequest
    }
  }

  def get(id: Int) = WithAuthentication { (user, request) =>
    dataAccessObject.findById(id).map { d ⇒
      if (acceptsJson(request)) {
        Ok(Json.toJson(d)(jsonSerializer.jsonWriter))
      } else if (acceptsHtml(request)) {
        Ok(views.html.aria.namedentity.item(d.id, user, ariaController, pageTitle))
      } else {
        BadRequest
      }
    }.getOrElse(NotFound);
  }

  val createForm = Form(
    tuple(
      "name" -> nonEmptyText(minLength = 3),
      "displayName" -> optional(text),
      "description" -> optional(text),
      "enabled" -> boolean))
  val updateForm = Form(
    tuple(
      "name" -> nonEmptyText(minLength = 3),
      "displayName" -> optional(text),
      "description" -> optional(text),
      "enabled" -> boolean,
      "version" -> number))

  def create = WithAuthentication { implicit request =>
    createForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, displayName, description, enabled) =>
          val dt: MODEL = modelBuilder(name, displayName, description, enabled)
          val id = dataAccessObject.insert(dt)
          Ok(s"""{"id"=id}""")
      })
  }

  def update(id: Int) = WithAuthentication { implicit request =>
    updateForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, displayName, description, enabled, version) =>
          val dtp: PERSISTED = persistedBuilder(id, name, displayName, description, enabled, version = version)
          //Simcards.up
          dataAccessObject.update(dtp) match {
            case true => Ok(s"$playController $id updated successfully")
            case _    => NotFound
          }
      })
  }

  def delete(id: Int) = WithAuthentication {
    dataAccessObject.deleteById(id) match {
      case true ⇒ Ok(s"$playController $id deleted successfully")
      case _    ⇒ NotFound
    }
  }
}
