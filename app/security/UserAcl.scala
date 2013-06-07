package security

import playguard._

import play.api.libs.json.JsValue

case class UserAcl[U](implicit cx: UserAuthorizationContext[U]) {
  case class IsMemberOf(name: String) extends UserAuthorizationRule[U] {
    def eval: Boolean = {
      cx.user == "ciccio"
    }
  }

  case class CanUse(feature: String) extends UserAuthorizationRule[U] {
    def eval = true
  }

  case class UserCanManageDevice[U](implicit val cx: RequestHeaderAuthorizationContext[U]) extends RequestHeaderAuthorizationRule[U] {
    def eval = {
      val deviceId = cx.request.getQueryString("deviceId")
      //cx.user.canManageDevice(deviceId)
      cx.request.getQueryString("ciccio").exists(_ == "buffo")
    }
  }


  case class JsonCheckSomething[U](implicit val cx: RequestAuthorizationContext[U, JsValue]) extends RequestAuthorizationRule[U, JsValue] {
    def eval = true
  }
  
  val AclDeviceProvisioning = Acl(
			CanUse("device.create"),
			CanUse("device.destroy")
	)
	
	val AclDeviceUpdate = Acl(
			CanUse("device.update")
	)
	
	val AclDeviceAdmin = Acl(
			AclDeviceProvisioning,
			AclDeviceUpdate
	)
}
