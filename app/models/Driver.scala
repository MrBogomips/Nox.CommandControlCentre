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

trait DriverTrait extends Validatable {
  val name: String
  val surname: String
  val displayName: String
  val enabled: Boolean

  def validate = {
    Nil
  }
}

case class Driver(name: String, surname: String, displayName: String, enabled: Boolean)
  extends DriverTrait
  with ValidationRequired {
  def this(name: String, surname: String, enabled: Boolean = true) = 
    this(name, surname, s"$surname $name", enabled)
}

case class DriverPersisted(id: Int, name: String, surname: String, displayName: String, enabled: Boolean, creationTime: Timestamp, modificationTime: Timestamp, version: Int)
  extends DriverTrait
  with Persistable

object Drivers
  extends Table[DriverPersisted]("drivers")
  with Backend {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def surname = column[String]("surname")
  def displayName = column[String]("display_name")
  def enabled = column[Boolean]("enabled")
  def _ctime = column[Timestamp]("_ctime")
  def _mtime = column[Timestamp]("_mtime")
  def _ver = column[Int]("_ver")

  def * = id ~ name ~ surname ~ displayName ~ enabled ~ _ctime ~ _mtime ~ _ver <> (DriverPersisted, DriverPersisted.unapply _)
  def forInsert = name ~ surname ~ displayName ~ enabled <> (Driver, Driver.unapply _)
  def forUpdate = *
  
  def qyFindById(id: Int) = (for { d <- Drivers if (d.id === id)} yield d)

  def findAll: Seq[DriverPersisted] = db withSession (for { d <- Drivers } yield d).list

  def findAllEnabled: Seq[DriverPersisted] = db withSession (for { d <- Drivers if (d.enabled) } yield d).list

  def findById(id: Int): Option[DriverPersisted] = db withSession qyFindById(id).firstOption

  def insert(d: Driver): Either[Seq[ValidationError], Int] = WithValidation(d).right.map { d =>
    db withSession {
      Drivers.forInsert returning id insert d
    }
  }

  def update(obj: DriverPersisted): Either[Seq[ValidationError], Int] =  WithValidation(obj).right.map { dp =>
    db withSession {
      val sql = sqlu"""
	   UPDATE #$tableName
	       SET name = ${obj.name},
      		   surname = ${obj.surname},
	           display_name = ${obj.displayName},
	    	   enabled = ${obj.enabled},
	           _mtime = NOW(),
	           _ver = _ver + 1 
		 WHERE id = ${dp.id}
		 """
      executeUpdate(s"driver $obj", sql)
    }
  }
  
  def updateWithVersion(obj: DriverPersisted): Either[Seq[ValidationError], Int] =  WithValidation(obj).right.map { dp =>
    db withSession {
      val sql = sqlu"""
	   UPDATE #$tableName
	       SET name = ${obj.name},
      		   surname = ${obj.surname},
	           display_name = ${obj.displayName},
	    	   enabled = ${obj.enabled},
	           _mtime = NOW(),
	           _ver = _ver + 1 
		 WHERE id = ${dp.id}
		   AND _ver = ${dp.version}
		 """
      executeUpdate(s"driver $obj", sql)
    }
  }
  
  def deleteById(id: Int): Int = db withSession {
    qyFindById(id).delete
  }
}
	
	
