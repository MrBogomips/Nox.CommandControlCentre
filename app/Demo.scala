package globals

object Demo {
  private def dk(k: String) = s"nox.ccc.demo.$k"
  lazy val isEnabled = Configuration.getBoolean(dk("is_demo_mode"))
  lazy val applicationKey = Configuration.getString(dk("app_key"))
  lazy val applicationId = Configuration.getString(dk("app_id")) 
  lazy val userId = Configuration.getString(dk("user_id"))
  lazy val sessionId = Configuration.getString(dk("session_id"))
  lazy val mqttClientTopic = s"NOXSPHERA"
  lazy val mqttApplicationTopic = s"$mqttClientTopic-APPID$applicationId"
  lazy val mqttUserTopic = s"$mqttApplicationTopic-UID$userId"
  lazy val mqttSessionTopic = s"$mqttUserTopic-SID$sessionId"
}