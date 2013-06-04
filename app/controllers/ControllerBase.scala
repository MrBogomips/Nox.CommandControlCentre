package controllers

import play.api._
import play.api.mvc._

/**
 * Base controller
 */
trait ControllerBase extends Controller {
  /**
   * A shortcut to the application configuration
   */
  val conf = play.Configuration.root()
  
  /**
   * Authentication call back parameter name used to redirect clients to requested URL
   */
  lazy val auth_cb = conf.getString("nox.ccc.security.login_cb_param", "_cb")
  
  /**
   * Default uri upon a successfull authentication
   */
  lazy val default_auth_uri = conf.getString("nox.ccc.security.default_auth_uri", "/")
}