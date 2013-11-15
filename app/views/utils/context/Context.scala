package views.utils.context

import play.api._
import play.api.mvc._
import models.UserPersisted

trait ProvidesViewContext {
  //implicit def context[A](implicit request: Request[A]): ViewContext[A] = ViewContext(None, request)
  implicit def context[A](implicit request: Request[A], user: UserPersisted) = ViewContext(Some(user))
}

case class ViewContext (user: Option[UserPersisted]) {
  lazy val isAnonymous = !user.isDefined
}