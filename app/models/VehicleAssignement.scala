package models

import patterns.models._
import play.api.Logger
import play.api.db._
import play.api.Play.current
import java.sql.Timestamp
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{ GetResult, StaticQuery => Q }
import Q.interpolation
import org.postgresql.util.PSQLException
import org.joda.time.DateTime
import utils.Converter._

import com.github.nscala_time.time.Imports._
import com.github.tototoshi.slick.JodaSupport._

trait VehicleAssignementTrait extends Validatable {
  val vehicleId: Int
  val driverId: Int
  val beginAssignement: DateTime
  val endAssignement: DateTime
  val enabled: Boolean

  def validate {
     validateLesserOrEqualThan("beginAssignement", beginAssignement, "endAssignement", endAssignement)
  }
}

case class VehicleAssignement(vehicleId: Int, driverId: Int, beginAssignement: DateTime, endAssignement: DateTime, enabled: Boolean)
  extends VehicleAssignementTrait
  with Model[VehicleAssignementTrait]

case class VehicleAssignementPersisted(id: Int, vehicleId: Int, driverId: Int, beginAssignement: DateTime, endAssignement: DateTime, enabled: Boolean, creationTime: Timestamp = new Timestamp(0), modificationTime: Timestamp = new Timestamp(0), version: Int)
  extends VehicleAssignementTrait
  with Persisted[VehicleAssignement]

object VehicleAssignements
  extends EnabledEntityCrudTable[VehicleAssignementTrait, VehicleAssignement, VehicleAssignementPersisted]("VehiclesDrivers") {

  def vehicleId = column[Int]("vehicleId")
  def driverId = column[Int]("driverId")
  def beginAssignement = column[DateTime]("beginAssignement")
  def endAssignement = column[DateTime]("endAssignement")

  def * = id ~ vehicleId ~ driverId ~ beginAssignement ~ endAssignement ~ enabled ~ creationTime ~ modificationTime ~ version <> (VehicleAssignementPersisted, VehicleAssignementPersisted.unapply _)
  def forInsert = vehicleId ~ driverId ~ beginAssignement ~ endAssignement ~ enabled ~ creationTime ~ modificationTime ~ version <> (
    { t => VehicleAssignement(t._1, t._2, t._3, t._4, t._5) },
    { (o: VehicleAssignement) =>
      {
        import java.util.Date
        val now = new Timestamp(new Date().getTime())
        Some((o.vehicleId, o.driverId, o.beginAssignement, o.endAssignement, o.enabled, now, now, 0))
      }
    })

  implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = { e => ??? }

  // TODO: reimplement avoiding explicit SQL
  def update(obj: VehicleAssignementPersisted): Boolean = WithValidation(obj) { vobj =>
    db withSession {
      val sql = sqlu"""
	   UPDATE "#$tableName"
	       SET "vehicleId" = ${vobj.vehicleId},
      		   "driverId" = ${vobj.driverId},
      		   "beginAssignement" = ${vobj.beginAssignement},
      		   "endAssignement" = ${vobj.endAssignement},
	    	   enabled = ${vobj.enabled},
	           "modificationTime" = NOW(),
	           version = version + 1 
		 WHERE id = ${vobj.id}
		 """
      executeUpdate(s"driver $vobj", sql) == 1
    }
  }
  // TODO: reimplement avoiding explicit SQL
  def updateWithVersion(obj: VehicleAssignementPersisted): Boolean = WithValidation(obj) { vobj =>
    db withSession {
      val sql = sqlu"""
	   UPDATE "#$tableName"
	       SET "vehicleId" = ${vobj.vehicleId},
      		   "driverId" = ${vobj.driverId},
      		   "beginAssignement" = ${vobj.beginAssignement},
      		   "endAssignement" = ${vobj.endAssignement},
	    	   enabled = ${vobj.enabled},
	           "modificationTime" = NOW(),
	           version = version + 1 
		 WHERE id = ${vobj.id}
		   AND version = ${vobj.version}
		 """
      executeUpdate(s"driver $vobj", sql) == 1
    }
  }
}
	
	
