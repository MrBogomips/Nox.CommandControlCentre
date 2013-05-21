package globals

import play.api._

object Application {
  lazy val isDemoMode = Demo.isEnabled
  lazy val applicationKey = if (!isDemoMode) Configuration.getString("nox.ccc.app_key") else Demo.applicationKey
  lazy val applicationId = if (!isDemoMode) Configuration.getString("nox.ccc.app_id") else Demo.applicationId
  lazy val eventsWebSocket = Configuration.getString("nox.ccc.events_ws_uri")
}