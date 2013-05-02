package test

import models._
 
object DeviceCommandRequest {
	val dc = new DeviceCommandRequest("ciccio", "buffo", List(Argument("arg1", "value1"), Argument("arg2", "value2")))
                                                  //> dc  : models.DeviceCommandRequest = DeviceCommandRequest(ciccio,buffo,List(A
                                                  //| rgument(arg1,value1), Argument(arg2,value2)))
                                                         
  dc.prepareSendingMessage                        //> res0: String = {"deviceId":"ciccio","command":"buffo","sent_time":"2013-04-2
                                                  //| 3 11:42Z","arguments":[{"name":"arg1","value":"value1"},{"name":"arg2","valu
                                                  //| e":"value2"}]}
}