
function init_listner(topic) {
	
	
	var devFact = new DeviceFactory();
	
	//DeviceFactory = new DeviceRecordFactory(oll_markers);

	devices = {};
    socket = io.connect(MQTT_BROKER_URI); // togliere l'MQTT broker hardcoded 
  
    socket.on('connect', function () {
        socket.on('mqtt', function (msg) {
        	
	        //console.log(msg.topic+' '+msg.payload);
	  		
	        var jData = $.parseJSON(msg.payload);
	        
	  		
	  		var e;
	  		if((e = devices[jData.device]) == undefined)
	  		{
	  			// new device found!
	  			e = devices[jData.device]=devFact.buildDevice(jData);
	  			
	  		}
	  		else
	  		{
	  			e.updateData(jData);
	  		}
        });
        
        //socket.emit('subscribe',{topic: topic});
        
      });
    
    
}