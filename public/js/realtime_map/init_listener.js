
function init_listner(topic) {
		
	var devFact = new DeviceFactory();

	devices = {};
    socket = io.connect(MQTT_BROKER_URI);
  
    socket.on('connect', function () {
        socket.on('mqtt', function (msg) {
        	
	        var jData = $.parseJSON(msg.payload);
	        var tokens = msg.topic.split("/");
	       
	        //The message type is the last one token in array
	        var messageType = tokens[0];
	        
	  		var e;
	  		if((e = devices[jData.device]) == undefined)
	  		{
	  			// new device found!
	  			e = devices[jData.device]=devFact.buildDevice(messageType, jData);
	  			
	  		}
	  		else
	  		{
	  			e.updateData(messageType, jData);
	  		}
        });
            
      });
    
}