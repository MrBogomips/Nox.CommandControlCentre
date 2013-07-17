package controllers.biz

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import controllers._
import models.{Devices, Device ⇒ DeviceModel, DevicePersisted, DeviceInfoPersisted}

import org.joda.time.format.ISODateTimeFormat

object Device extends ControllerBase {
  import models.json.deviceInfoChannelPersistedSerializer

  def index(all: Boolean = false) = Action { implicit request ⇒
    implicit val req = request
    val devices = all match {
      case false ⇒ Devices.find(Some(true))
      case true ⇒ Devices.find(None)
    }
      Ok(Json.toJson(devices))
  }
  def get(id: Int) = Action { implicit request ⇒
    implicit val req = request
    Devices.findById(id).map { d ⇒
        Ok(Json.toJson(d))
    }.getOrElse(NotFound);
  }
  def getByName(name: String) = Action { implicit request ⇒
    implicit val req = request
    Devices.findByName(name).map { d ⇒
        Ok(Json.toJson(d))
    }.getOrElse(NotFound);
  }
}