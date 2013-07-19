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

trait VehicleAssignementTrait extends Validatable {
  val vehicleId: Int
  val driverId: Int
  val beginAssignement: DateTime
  val endAssignement: DateTime
  val enabled: Boolean

  def validate {
    import collection.mutable.{ HashMap, MultiMap, Set }
    val errorsAccumulator = new HashMap[String, Set[String]] with MultiMap[String, String]
    // Provide all the rule checked
    if (endAssignement.isBefore(beginAssignement))
      errorsAccumulator.addBinding("endAssignement", "End assignement must follow the begin")

  }
}

case class VehicleAssignement(vehicleId: Int, driverId: Int, beginAssignement: DateTime, endAssignement: DateTime, enabled: Boolean)
  extends VehicleAssignementTrait
  with Model[VehicleAssignementTrait]

case class VehicleAssignementPersisted(id: Int, vehicleId: Int, driverId: Int, beginAssignement: DateTime, endAssignement: DateTime, enabled: Boolean, creationTime: Timestamp = new Timestamp(0), modificationTime: Timestamp = new Timestamp(0), version: Int)
  extends VehicleAssignementTrait
  with Persisted[VehicleAssignement]

object VehicleAssignements
  extends Table[VehicleAssignementPersisted]("VehiclesDrivers")
  with Backend
  with CrudOperations[VehicleAssignementTrait, VehicleAssignement, VehicleAssignementPersisted] {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def vehicleId = column[Int]("vehicleId")
  def driverId = column[Int]("driverId")
  def beginAssignement = column[DateTime]("beginAssignement")
  def endAssignement = column[DateTime]("endAssignement")
  def enabled = column[Boolean]("enabled")
  def creationTime = column[Timestamp]("creationTime")
  def modificationTime = column[Timestamp]("modificationTime")
  def version = column[Int]("version")

  def * = id ~ vehicleId ~ driverId ~ beginAssignement ~ endAssignement ~ enabled ~ creationTime ~ modificationTime ~ version <> (VehicleAssignementPersisted, VehicleAssignementPersisted.unapply _)
  def forInsert = vehicleId ~ driverId ~ beginAssignement ~ endAssignement ~ enabled <> (VehicleAssignement, VehicleAssignement.unapply _)
  def forUpdate = *

  implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = { e => ??? }

  def qyFindById(id: Int) = (for { va <- VehicleAssignements if (va.id === id) } yield va)
  
  def find(enabled: Option[Boolean] = None): Seq[VehicleAssignementPersisted] = db withSession {
    val qy = enabled match {
      case None     => for { v <- VehicleAssignements } yield v
      case Some(en) => for { v <- VehicleAssignements if (v.enabled === en) } yield v
    }
    qy.list
  }
  
  def findById(id: Int): Option[VehicleAssignementPersisted] = db withSession qyFindById(id).firstOption

  def insert(va: VehicleAssignement): Int = WithValidation(va) { va =>
    db withSession {
      VehicleAssignements.forInsert returning id insert va
    }
  }

  def update(obj: VehicleAssignementPersisted): Boolean = WithValidation(obj) { vobj =>
    db withSession {
      val sql = sqlu"""
	   UPDATE "#$tableName"
	       SET "vehicleId" = ${vobj.vehicleId},
      		   "driverId" = ${vobj.driverId},
      		   "beginAssignement" = ${jodaDateTimeToTimestamp(vobj.beginAssignement)},
      		   "endAssignement" = ${jodaDateTimeToTimestamp(vobj.endAssignement)},
	    	   enabled = ${vobj.enabled},
	           "modificationTime" = NOW(),
	           version = version + 1 
		 WHERE id = ${vobj.id}
		 """
      executeUpdate(s"driver $vobj", sql) == 1
    }
  }

  def updateWithVersion(obj: VehicleAssignementPersisted): Boolean = WithValidation(obj) { vobj =>
    db withSession {
      val sql = sqlu"""
	   UPDATE "#$tableName"
	       SET "vehicleId" = ${vobj.vehicleId},
      		   "driverId" = ${vobj.driverId},
      		   "beginAssignement" = ${jodaDateTimeToTimestamp(vobj.beginAssignement)},
      		   "endAssignement" = ${jodaDateTimeToTimestamp(vobj.endAssignement)},
	    	   enabled = ${vobj.enabled},
	           "modificationTime" = NOW(),
	           version = version + 1 
		 WHERE id = ${vobj.id}
		   AND version = ${vobj.version}
		 """
      executeUpdate(s"driver $vobj", sql) == 1
    }
  }

  def deleteById(id: Int): Boolean = db withSession {
    val sql = sqlu"""DELETE FROM "#$tableName" WHERE id = $id"""
    executeDelete("Deleting from #$tableName record identified by $id", sql) == 1
  }
}
	
	
