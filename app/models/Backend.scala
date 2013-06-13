package models
package models

//import play.api.Logger
import play.api.db._
import play.api.Play.current
import scala.slick.session.Database


trait Backend {
  val db = Database.forDataSource(DB.getDataSource())
}