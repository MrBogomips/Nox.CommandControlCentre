package models

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

trait DriverTrait extends Validatable {
  val name: String
  val surname: String
  val displayName0: Option[String]
  val displayName: String = displayName0.map(v => v).getOrElse(name)
  val enabled: Boolean

  def validate {
    validateMinLength("name", name, 3)
    validateMinLength("surname", surname, 3)
    validateMinLength("displayName", displayName, 3)
  }
}

case class Driver(name: String, surname: String, displayName0: Option[String], enabled: Boolean)
  extends DriverTrait
  with Model[DriverTrait] {
  def this(name: String, surname: String, enabled: Boolean = true) = 
    this(name, surname, Some(s"$surname $name"), enabled)
}

case class DriverPersisted(id: Int, name: String, surname: String, displayName0: Option[String], enabled: Boolean, creationTime: Timestamp = new Timestamp(0), modificationTime: Timestamp = new Timestamp(0), version: Int)
  extends DriverTrait
  with Persistable[DriverTrait]

object Drivers
  extends Table[DriverPersisted]("drivers")
  with Backend 
  with NameEntityCrudOperations[DriverTrait, Driver, DriverPersisted] {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def surname = column[String]("surname")
  def displayName = column[String]("display_name")
  def enabled = column[Boolean]("enabled")
  def _ctime = column[Timestamp]("_ctime")
  def _mtime = column[Timestamp]("_mtime")
  def _ver = column[Int]("_ver")

  def * = id ~ name ~ surname ~ displayName.? ~ enabled ~ _ctime ~ _mtime ~ _ver <> (DriverPersisted, DriverPersisted.unapply _)
  def forInsert = name ~ surname ~ displayName.? ~ enabled <> (Driver, Driver.unapply _)
  def forUpdate = *
  
  private implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = {e => ???}
  
  def qyFindById(id: Int) = (for { d <- Drivers if (d.id === id)} yield d)

  def find(enabled: Option[Boolean] = None): Seq[DriverPersisted] = db withSession {
    val qy = enabled match {
      case None     => for { d <- Drivers } yield d
      case Some(en) => for { d <- Drivers if (d.enabled === en) } yield d
    }
    qy.list
  }
  
  def findById(id: Int): Option[DriverPersisted] = db withSession qyFindById(id).firstOption

  def findByName(name: String): Option[DriverPersisted] = db withSession {
    val qy = for { d <- Drivers if (d.name === name) } yield d
    qy.firstOption
  }
  
  def insert(d: Driver): Int = WithValidation(d) { d =>
    db withSession {
      Drivers.forInsert returning id insert d
    }
  }

  def update(obj: DriverPersisted): Boolean =  WithValidation(obj) { vobj =>
    db withSession {
      val sql = sqlu"""
	   UPDATE #$tableName
	       SET name = ${vobj.name},
      		   surname = ${vobj.surname},
	           display_name = ${vobj.displayName},
	    	   enabled = ${vobj.enabled},
	           _mtime = NOW(),
	           _ver = _ver + 1 
		 WHERE id = ${vobj.id}
		 """
      executeUpdate(s"driver $vobj", sql) == 1
    }
  }
  
  def updateWithVersion(obj: DriverPersisted): Boolean =  WithValidation(obj) { vobj =>
    db withSession {
      val sql = sqlu"""
	   UPDATE #$tableName
	       SET name = ${vobj.name},
      		   surname = ${vobj.surname},
	           display_name = ${vobj.displayName},
	    	   enabled = ${vobj.enabled},
	           _mtime = NOW(),
	           _ver = _ver + 1 
		 WHERE id = ${vobj.id}
		   AND _ver = ${vobj.version}
		 """
      executeUpdate(s"driver $vobj", sql) == 1
    }
  }
  
  def deleteById(id: Int): Boolean = db withSession {
    val sql = sqlu"DELETE FROM #$tableName WHERE id = $id"
    executeDelete("Deleting from #$tableName record identified by $id", sql) == 1
  }
}
	
	
