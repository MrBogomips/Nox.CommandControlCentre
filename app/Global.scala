import play.api._

package object globals {
  lazy val Configuration = play.Configuration.root()
}



object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application has started")
  }  
  
  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }  
    
}