package object playguard {
  /**
   * Utility class to define conditional blocks based on AuthorizationRules
   */
  case class WithPolicy(rule: AuthorizationRule) {
    /**
     * Execute the block if the rule applies
     */
    def apply(f: => Unit): Unit = if (rule.eval) f
    /**
     * Execute the block only if the rule applies
     */
    def apply[A](f: => A): Option[A] = if (rule.eval) Some(f) else None
  }

  /**
   * Execute the first block if the rule applies, otherwise the «else» block
   */
  case class WithPolicyElse(rule: AuthorizationRule) {
    def apply(f: => Unit)(els: => Unit): Unit = if (rule.eval) f else els
  }

  /**
   * An utility method to check if a policy holds or not
   */
  def IsPolicyPassed(rule: AuthorizationRule) = rule.eval
}