package controllers

import models.{ DeviceGroupTrait, DeviceGroup => DeviceGroupModel, DeviceGroupPersisted, DeviceGroups }
import models.json.DeviceGroupPersistedSerializer

object DeviceGroup extends NamedEntityController[DeviceGroupTrait, DeviceGroupModel, DeviceGroupPersisted] {
  override val ariaController = "devicegroups"
  val pageTitle = "Device Group"
  //val playController = "DeviceGroup" // match the object name

  val dataAccessObject = DeviceGroups
  implicit val jsonSerializer = DeviceGroupPersistedSerializer

  def modelBuilder(name: String, displayName: Option[String], description: Option[String], enabled: Boolean) = {
    DeviceGroupModel(name, displayName, description, enabled)
  }

  def persistedBuilder(id: Int, name: String, displayName: Option[String], description: Option[String], enabled: Boolean, version: Int) = {
    DeviceGroupPersisted(id, name, displayName, description, enabled, version = version)
  }
}
 
/*
object DeviceGroupOLD extends Secured {
  val ariaController = "devicegroups"
  val pageTitle = "Device Group"
  val playController = "DeviceGroup" // match the object name

  type TRAIT = DeviceGroupTrait
  type MODEL = DeviceGroupModel
  type PERSISTED = DeviceGroupPersisted

  val dataAccessObject: NamedEntities[TRAIT, MODEL, PERSISTED] = DeviceGroups
  implicit val jsonSerializer: NamedEntityPersistedSerializer[PERSISTED] = DeviceGroupPersistedSerializer

  def modelBuilder(name: String, displayName: Option[String], description: Option[String], enabled: Boolean): MODEL = {
    DeviceGroupModel(name, displayName, description, enabled)
  }

  def persistedBuilder(id: Int, name: String, displayName: Option[String], description: Option[String], enabled: Boolean, version: Int): PERSISTED = {
    DeviceGroupPersisted(id, name, displayName, description, enabled, version = version)
  }

  /// BEGIN COMMON METHODS
  def index(all: Boolean = false) = WithAuthentication { (user, request) =>
    implicit val req = request
    val entities: Seq[PERSISTED] = all match {
      case false => dataAccessObject.find(Some(true))
      case true  => dataAccessObject.find(None)
    }
    if (acceptsJson(request)) {
      Ok(Json.toJson(entities))
    } else if (acceptsHtml(request)) {
      Ok(views.html.aria.namedentity.index(entities, user, ariaController, pageTitle, playController))
    } else {
      BadRequest
    }
  }

  def get(id: Int) = WithAuthentication { (user, request) =>
    dataAccessObject.findById(id).map { d ⇒
      if (acceptsJson(request)) {
        Ok(Json.toJson(d))
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
*/

