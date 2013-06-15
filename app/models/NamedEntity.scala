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

trait NamedEntityTrait {
  val name: String
  val displayName: String
  val description: Option[String]
  val enabled: Boolean

  require(name.length >= 3, "name must be at least 3 characters")

  def normalizeName(initName: String) = initName.toLowerCase

}

abstract class SimpleNameEntityPersisted[T <: SimpleNameEntityPersisted[T, T2], T2 <: NamedEntityTrait]
  extends Persisted[T, T2]
  with NamedEntityTrait {
  val id: Int
  val name: String
  val displayName: String
  val description: Option[String]
  val enabled: Boolean
  val creationTime: Timestamp
  val modificationTime: Timestamp
  val version: Int

  /**
   * The specific table mapper to use to support basic CRUD operations
   */
  val entityTableMapper: SimpleNameEntityTableTrait[T, T2]

  def delete(): Boolean = entityTableMapper.deleteById(id)
  def refetch(): Option[T] = entityTableMapper.findById(id)
  def update(): Boolean = entityTableMapper.update(this.asInstanceOf[T])
  def updateWithVersion(): Boolean = entityTableMapper.updateWithVersion(this.asInstanceOf[T])

}

trait SimpleNameEntityTableTrait[T <: SimpleNameEntityPersisted[T, T2], T2 <: NamedEntityTrait]
  extends PersistedTableTrait[T, T2] {
  def findAllEnabled: Seq[T]
  def findByName(name: String): Option[T]
}

/**
 * A table mapper suitable to support ``SimpleNameEntityPersisted`` subtypes
 */
abstract case class SimpleNameEntityTable[T <: SimpleNameEntityPersisted[T, T2], T2 <: NamedEntityTrait](override val schemaName: Option[String], override val tableName: String, apply: SimpleNamedEntityApplyFn[T], unapply: SimpleNamedEntityUnapplyFn[T])
  extends PersistedTable[T, T2](schemaName, tableName)
  with SimpleNameEntityTableTrait[T, T2] {
  def this(tableName: String, apply: SimpleNamedEntityApplyFn[T], unapply: SimpleNamedEntityUnapplyFn[T]) = this(None, tableName, apply, unapply)

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def displayName = column[String]("display_name", O.Nullable)
  def description = column[String]("display_name", O.Nullable)
  def enabled = column[Boolean]("enabled")
  def _ctime = column[Timestamp]("_ctime")
  def _mtime = column[Timestamp]("_mtime")
  def _ver = column[Int]("_ver")

  def * = id ~ name ~ displayName ~ description.? ~ enabled ~ _ctime ~ _mtime ~ _ver <> (apply, unapply)

  private implicit val getResult = GetResult(r => apply(r.nextInt, r.nextString, r.nextString, r.nextStringOption, r.nextBoolean, r.nextTimestamp, r.nextTimestamp, r.nextInt))

  def findAll: Seq[T] = db withSession {
    val sql = sql"SELECT id, name, display_name, description, enabled, _ctime, _mtime, _ver FROM #$tableName"
    executeSql(BackendOperation.SELECT, s"$tableName ${this.toString}", sql.as[T]) { _.list }
  }
  def findAllEnabled: Seq[T] = db withSession {
    val sql = sql"SELECT id, name, display_name, description, enabled, _ctime, _mtime, _ver FROM #$tableName WHERE enabled = true"
    executeSql(BackendOperation.SELECT, s"$tableName ${this.toString}", sql.as[T]) { _.list }
  }
  def findById(id: Int): Option[T] = db withSession {
    val sql = sql"SELECT id, name, display_name, description, enabled, _ctime, _mtime, _ver FROM #$tableName WHERE id = $id"
    executeSql(BackendOperation.SELECT, s"$tableName ${this.toString}", sql.as[T]) { _.firstOption }
  }
  def findByName(name: String): Option[T] = db withSession {
    val sql = sql"SELECT id, name, display_name, description, enabled, _ctime, _mtime, _ver FROM #$tableName WHERE name = $name"
    executeSql(BackendOperation.SELECT, s"$tableName ${this.toString}", sql.as[T]) { _.firstOption }
  }

  def insert(obj: T2): Int = db withSession {
    val sql = sql"""
    INSERT INTO #$tableName (
    		name, 
    		display_name,
    		description, 
    		enabled, 
    		_ctime,
    		_mtime,
    		_ver
    ) 
    VALUES (
    		${obj.name},
    		${obj.displayName},
    		${obj.description},
    		${obj.enabled},
    		NOW(),
    		NOW(),
    		0
    )
    RETURNING id
    """

    executeSql(BackendOperation.INSERT, s"$tableName $obj", sql.as[Int]) { _.first }
  }

  def delete(obj: T): Boolean = deleteById(obj.id)
  def deleteById(id: Int): Boolean = db withSession {
    val sql = sqlu"DELETE FROM #$tableName WHERE id = $id"
    executeDelete("Deleting from #$tableName record identified by $id", sql) == 1
  }

  def updateWithVersion(obj: T): Boolean = withPersistableObject(obj, default = false) {
    db withSession {
      val sql = sqlu"""
    UPDATE  #$tableName
       SET name = ${obj.name},
    	   display_name = ${obj.displayName},
    	   description = ${obj.description},
    	   enabled = ${obj.enabled},
           _mtime = NOW(),
           _ver = _ver + 1 
	 WHERE id = ${obj.id}
	   AND _ver = ${obj.version}
	 """
      executeUpdate(s"$tableName $obj", sql) == 1
    }
  }
  def update(obj: T): Boolean = withPersistableObject(obj, default = false) {
    db withSession {
      val sql = sqlu"""
    UPDATE  #$tableName
       SET name = ${obj.name},
    	   display_name = ${obj.displayName},
    	   description = ${obj.description},
    	   enabled = ${obj.enabled},
           _mtime = NOW(),
           _ver = _ver + 1 
	 WHERE id = ${obj.id}
	 """
      executeUpdate(s"$tableName $obj", sql) == 1
    }
  }
}
