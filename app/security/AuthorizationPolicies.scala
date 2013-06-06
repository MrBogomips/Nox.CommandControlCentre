package security

import security._
import play.api.libs.json.JsValue
import playguard.AuthorizationRule
import playguard.AuthorizationContext

case class IsMemberOf(name: String) extends AuthorizationRule {
  def eval[U,C](implicit cx : AuthorizationContext[U,C]): Boolean = {
	cx.user == name
  }
}

case class CanUse(feature: String) extends AuthorizationRule {
  def eval[U,C](implicit cx : AuthorizationContext[U,C]): Boolean = {
	true
  }
}

case object UserCanManageDevice extends AuthorizationRule {
  def eval[U,C](implicit cx : AuthorizationContext[U,C]): Boolean = {
	val deviceId = cx.request.getQueryString("deviceId")
	//cx.user.canManageDevice(deviceId)
	true
  }
}

case object AclDefault extends AuthorizationRule {
  def eval[U,C](implicit cx : AuthorizationContext[U,C]): Boolean = {
	// Todo
    true
  }
}
 
case object JsonCheckSomething extends AuthorizationRule {
  def eval[U,C](implicit cx : AuthorizationContext[U,C]): Boolean = {
	// Todo
    
    
    true
  }
}