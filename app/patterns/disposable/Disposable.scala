package patterns.disposable

/**
 * A disposable class must provide a dispose method invoked to clean-up resources
 * 
 * Implementers are responsible to check is the current object has been already disposed.
 */
trait Disposable {
  def dispose: Unit
}

/**
 * Enable the pattern
 */
object WithDisposition {
  def apply[A <: Disposable, B](obj: A)(b: A => B) {
    try {
      val r = b(obj)
    } finally {
      obj.dispose
    }
  }
  /*
  def apply[A <: Disposable, B](o1: A, o2: A)(b: (A, A) => B) {
    try {
      val r = b(o1, o2)
    } finally {
      o1.dispose
      o2.dispose
    }
  }*/
}