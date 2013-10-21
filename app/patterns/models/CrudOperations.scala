package patterns.models

import scala.slick.driver.PostgresDriver.simple._

import org.postgresql.util.PSQLException
import Database.threadLocalSession
import java.sql.Timestamp

/**
  * Represents basic CRUD operations supported by all DAOs
  * 
  * @tparam A Represents the model contract
  * @tparam B Represents the model class
  * @tparam C Represents the persisted class
  */
trait CrudOperations[A <: Validatable, B <: Model[A], C <: Persisted[B]] {
  /**
   * retrieves all the entities
   */
  def index: Seq[C]
  /**
   * retrieves an entity based on the surrogated id
   */
  def findById(id: Int): Option[C]
  /**
   * add a model to the persistance layer
   */
  def insert(uobj: B): Int
  /**
   * update a persisted entity. this method must be implemented specifically
   */
  def update(uobj: C): Boolean
  /**
   * update a persisted entity. this method must be implemented specifically by checking the version
   */
  def updateWithVersion(uobj: C): Boolean
  /**
   * delete a persisted entity
   */
  def delete(obj: C): Boolean = deleteById(obj.id)
  /**
   * delete a persisted entity by its surrogated id
   */
  def deleteById(id: Int): Boolean
}

trait EnabledEntityCrudOperations [A <: Validatable, B <: Model[A], C <: Persisted[B]] 
  extends CrudOperations[A, B, C] {
  /**
   * retrieves an entity based on the alternate key
   */
  def find(enabled: Option[Boolean] = None): Seq[C]
}


/**
 * Represents CRUD operation supported by entities that can be enabled or not
 * 
 * @tparam A Represents the model contract
 * @tparam B Represents the model class
 * @tparam C Represents the persisted class
 */
trait NameEntityCrudOperations[A <: Validatable, B <: Model[A], C <: Persisted[B]]
  extends EnabledEntityCrudOperations[A, B, C] {
  def findByName(name: String): Option[C]
}

/**
 * Basic Data Access Object that prvide CRUD operations to every kind of entity
 */
abstract class CrudTable[TRAIT <: Validatable, MODEL <: Model[TRAIT], PERSISTED <: Persisted[MODEL]](tableName: String)
  extends Table[PERSISTED](tableName)
  with Backend
  with CrudOperations[TRAIT, MODEL, PERSISTED] {
  
  /**
   * By convention each entity is identified by a surrogates auto incremental identifier
   */
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  /**
   * By convention each entity has creation timestamp
   */
  def creationTime = column[Timestamp]("creationTime")
  /**
   * By convention each entity has a last modification time
   */
  def modificationTime = column[Timestamp]("modificationTime")
  /**
   * By convention each entity has a version that is incremented at each update
   */
  def version = column[Int]("version")
  
  def forInsert: scala.slick.lifted.ColumnBase[MODEL]
  
   /**
    * Exception mapper
    *
    * Maps a native postgres exception to a ValidationException.
    */
  protected implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing)
  
  
  def index: Seq[PERSISTED] = db withSession {
    val qy = for { dt <- this if (dt.id === id) } yield dt
    qy.list
  }
  
  def findById(id: Int): Option[PERSISTED] = db withSession {
    val qy = for { dt <- this if (dt.id === id) } yield dt
    qy.firstOption
  }

  def insert(uobj: MODEL): Int = WithValidation(uobj) { obj =>
    db withSession {
      this.forInsert returning this.id insert obj
    }
  }
  def deleteById(id: Int): Boolean = db withSession {
    val qy = for { d <- this if (d.id === id) } yield d
    qy.delete == 1
  }
}
/**
 * Basic Data Access Object that prvide CRUD operations to entity that can be enabled
 */
abstract class EnabledEntityCrudTable [TRAIT <: Validatable, MODEL <: Model[TRAIT], PERSISTED <: Persisted[MODEL]](tableName: String)
 extends CrudTable[TRAIT, MODEL, PERSISTED](tableName) {
  def enabled = column[Boolean]("enabled")
  
  def find(enabled: Option[Boolean] = None): Seq[PERSISTED] = db withSession {
    val qy = enabled match {
      case None     => for { d <- this } yield d
      case Some(en) => for { d <- this if (d.enabled === en) } yield d
    }
    qy.list 
  }
}