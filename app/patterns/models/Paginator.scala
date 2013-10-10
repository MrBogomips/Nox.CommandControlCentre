package patterns.models

import play.api.mvc.RequestHeader
import scala.slick.lifted.Query
import play.Configuration

/**
  * Represents a pagination request
  */
trait Pagination {
  def paginate[A, B](qy: Query[A, B]): Query[A, B]
}

/**
  * Build a pagination object in different ways
  */
object Paginator {
  private def ck(k: String) = s"nox.ccc.pagination.$k"
  lazy val requestSizeKey = Configuration.root().getString("request_size_key", "pz")
  lazy val requestIndexKey = Configuration.root().getString("request_index_key", "px")
  lazy val defaultPageSize = Configuration.root().getInt("default_page_size", 10)

  def defaultPaginator = defaultPageSize match {
    case pz if pz > 0  => FinitePagination(pz, 0)
    case pz if pz == 0 => NoPagination
    case _             => InvalidPagination
  }

  /**
    * A page size of 0 will generate a NoPagination object
    */
  def decode(size: Option[Int], index: Option[Int])(implicit default: Pagination = defaultPaginator): Pagination = (size, index) match {
    case (Some(size), Some(index)) if size > 0 => FinitePagination(size, index)
    case (Some(size), _) if size == 0          => default
  }
  /**
   * Attempt to parse the pagination from the querystring variables
   * 
   * See the configuration file for defining defaults
   */
  def fromRequest(implicit request: RequestHeader, default: Pagination = defaultPaginator): Pagination = {
    if (request.queryString.contains(requestSizeKey) && request.queryString.contains(requestIndexKey)) {
      val pageSizeString = request.queryString.get(requestSizeKey).flatMap(_.headOption).get
      val pageIndexString = request.queryString.get(requestIndexKey).flatMap(_.headOption).get
      if (pageSizeString.forall(_.isDigit) && pageIndexString.forall(_.isDigit)) {
        decode(Some(Integer.parseInt(pageSizeString)), Some(Integer.parseInt(pageIndexString)))(default)
      } else
        InvalidPagination
    } else
      default
  }
}

/**
  * Represents a finite pagination request
  */
case class FinitePagination(size: Int, index: Int) extends Pagination {
  require(size > 0, "Page size must be greather than zero")
  require(index >= 0, "Page index must be non negative")
  def paginate[A, B](qy: Query[A, B]): Query[A, B] = qy.drop(size * index).take(size)
}

/**
  * No pagination at all
  */
object NoPagination extends Pagination {
  def paginate[A, B](qy: Query[A, B]): Query[A, B] = qy
}
/**
  * This object is useful to configure the default behaviour to require a correct pagination specification
  */
object InvalidPagination extends Pagination {
  throw new ValidationException("paginator", "invalid pagination specification")
  def paginate[A, B](qy: Query[A, B]): Query[A, B] = ???
}