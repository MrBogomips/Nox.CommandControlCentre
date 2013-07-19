package object models {
  import java.sql.Timestamp
  
  type SimpleNamedEntityApplyFn[A] = (Int, String, String, Option[String], Boolean, Timestamp, Timestamp, Int) => A
  type SimpleNamedEntityUnapplyFn[A] = A => Option[(Int, String, String, Option[String], Boolean, Timestamp, Timestamp, Int)] 
}