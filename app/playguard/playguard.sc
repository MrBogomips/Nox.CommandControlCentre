package playguard

import models.Anonymous

object playguard {

  object Allow extends AuthorizationRule {
    def eval = true
    override def toString = "Allow"
  }

  class True extends AuthorizationRule {
    Console.println("  ...True.ctor()")
    def eval = {
      Console.println(">>True")
      true
    }
    override def toString = "True"
  }

  // A custom rule based on User
  import models.User
  case class MandrakeRule(implicit user: User) extends AuthorizationRule {
    def eval = user.login == "Mandrake"
  }

  // A custom rule based on Model
  case class Device(name: String) {
    def isManageableBy(implicit user: User) = true
  }

  case class CanManageDevice(implicit user: User, device: Device)
    extends AuthorizationRule {
    def eval = device.isManageableBy(user)
  }
  
  // Implicit context used to evaluate the rules
  implicit val user = Anonymous                   //> user  : models.Anonymous.type = User(anonymous,Anonymous,None,active,None)
  implicit val devXYZ = Device("xyz")             //> devXYZ  : playguard.playguard.Device = Device(xyz)
  
  WithPolicy(MandrakeRule() && CanManageDevice() ).apply {
    	// Executed if the user is Mandrake and can manipulate
    	// the device whithin the context
    }

  class False(implicit i: Int) extends AuthorizationRule {
    Console.println("  ...False.ctor()")
    def eval = {
      Console.println(">>False")
      false
    }
    override def toString = "False"
  }

  implicit val i = 1                              //> i  : Int = 1

  val rule1 = new True                            //>   ...True.ctor()
                                                  //| rule1  : playguard.playguard.True = True
  val rule2 = new False                           //>   ...False.ctor()
                                                  //| rule2  : playguard.playguard.False = False
  val rule3 = new False                           //>   ...False.ctor()
                                                  //| rule3  : playguard.playguard.False = False

  (Allow && Allow).eval                           //> res0: Boolean = true
  (new True && new True).eval                     //>   ...True.ctor()
                                                  //|   ...True.ctor()
                                                  //| >>True
                                                  //| >>True
                                                  //| res1: Boolean = true

  val rule12 = rule1 && rule2                     //> rule12  : playguard.And = And(True,False)
  val rule123 = rule1 && rule2 && rule3           //> rule123  : playguard.And = And(And(True,False),False)
  val rule12o3 = rule1 && rule2 || rule3          //> rule12o3  : playguard.Or = Or(And(True,False),False)
  val rule1o23 = rule1 || rule2 && rule3          //> rule1o23  : playguard.Or = Or(True,And(False,False))
  val rule1o23p = (rule1 || rule2) && rule3       //> rule1o23p  : playguard.And = And(Or(True,False),False)

  val rule123c = All(rule1, rule2, rule3)         //> rule123c  : playguard.All = All(WrappedArray(True, False, False))

  (new True && new False).eval                    //>   ...True.ctor()
                                                  //|   ...False.ctor()
                                                  //| >>True
                                                  //| >>False
                                                  //| res2: Boolean = false

  (new False && new False).eval                   //>   ...False.ctor()
                                                  //|   ...False.ctor()
                                                  //| >>False
                                                  //| res3: Boolean = false

  All(new False, new True).eval                   //>   ...False.ctor()
                                                  //|   ...True.ctor()
                                                  //| >>False
                                                  //| res4: Boolean = false

  All(new False, new False, new True).eval        //>   ...False.ctor()
                                                  //|   ...False.ctor()
                                                  //|   ...True.ctor()
                                                  //| >>False
                                                  //| res5: Boolean = false

  (new True || new False).eval                    //>   ...True.ctor()
                                                  //|   ...False.ctor()
                                                  //| >>True
                                                  //| res6: Boolean = true

  (new False && new True).eval                    //>   ...False.ctor()
                                                  //|   ...True.ctor()
                                                  //| >>False
                                                  //| res7: Boolean = false

  (new False || new True).eval                    //>   ...False.ctor()
                                                  //|   ...True.ctor()
                                                  //| >>False
                                                  //| >>True
                                                  //| res8: Boolean = true

  (new True && new False).eval                    //>   ...True.ctor()
                                                  //|   ...False.ctor()
                                                  //| >>True
                                                  //| >>False
                                                  //| res9: Boolean = false
}