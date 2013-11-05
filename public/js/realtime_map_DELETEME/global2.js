/*****************************************************************
 * 
 *  GLOBAL REFERENCES
 * 
 ****************************************************************/
var devices; 			// A collection of the devices seen so far
var socket; 			// Websocket to the push service 
var DeviceFactory;		// Singleton used to build new device record
var map;				// Reference to the OpenLayer Map
var markerLayer;		// OpenLayer layer used to place the  markers



/*****************************************************************
 * 
 *  TYPES DECLARATION
 * 
 ****************************************************************/

/*****************************************************************
 * 
 *  GLOBAL FUNCTIONS
 * 
 ****************************************************************/

	




function setTopic(topic){	
	//$('table#devices > tbody').empty();
	socket.emit('subscribe',{topic: topic});
}




