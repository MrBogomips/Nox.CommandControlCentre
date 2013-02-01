/*****************************************************************
 * 
 *  GLOBAL REFERENCES
 * 
 ****************************************************************/
var devices; 			// A collection of the devices seen so far
var socket; 			// Websocket to the push service 
var DeviceFactory;		// Singleton used to build new device record
var map;				// Reference to the OpenLayer Map
var markerLayer;		// OpenLayer layer used to place the markers

var projLonLat   = new OpenLayers.Projection("EPSG:4326");   // WGS 1984
var projMercator = new OpenLayers.Projection("EPSG:900913"); // Spherical Mercator

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


function updateTableRow(jData, numMsg, rate, startAt, lastAt, delay, speed){
	var coords = $.trim(jData.data.coords.lon) + ',' + $.trim(jData.data.coords.lat),
		deviceID = $.trim(jData.device),
		devicesTable = $('table#devices'),
		formattedStartAt, formattedLastAt, formattedLastDeviceTime;

	formattedStartAt = startAt.format("isoDateTime");
	formattedLastAt = lastAt.format("isoDateTime");
	
	formattedLastDeviceTime = jData.data.ts;
	
	
		devicesTable.find('tr#' + deviceID + ' > td.StartAt').text(formattedStartAt);
		devicesTable.find('tr#' + deviceID + ' > td.numMsg').text(numMsg);
		devicesTable.find('tr#' + deviceID + ' > td.rate').text(rate);
		
		devicesTable.find('tr#' + deviceID + ' > td.LastAt').text(formattedLastAt);
		devicesTable.find('tr#' + deviceID + ' > td.coords').text(coords);
		
		devicesTable.find('tr#' + deviceID + ' > td.LastDeviceTime').text(formattedLastDeviceTime);
		devicesTable.find('tr#' + deviceID + ' > td.Delay').text(delay);
		devicesTable.find('tr#' + deviceID + ' > td.Speed').text(speed);

}

function addTableRow(jData, numMsg, rate, startAt, lastAt, delay, speed){
	var deviceId =  $.trim(jData.device),
		coords = $.trim(jData.data.coords.lon) + ',' + $.trim(jData.data.coords.lat),
		devicesTable = $('table#devices'),
		newRow, formattedStartAt, formattedLastAt, formattedLastDeviceTime;
		
	//now.format("isoDateTime");
	
	formattedStartAt = startAt.format("isoDateTime");
	formattedLastAt = lastAt.format("isoDateTime");
	
	formattedLastDeviceTime = jData.data.ts;
	
	
		newRow = '<tr id="' + deviceId  + '" style="height: 20px;">' +
					'<td style="font-size: 12px;height: 100%;line-height: 20px; vertical-align: middle; font-weight: normal;">' + (devicesTable.find('tbody > tr').size() + 1) + '</td>' + 
					'<td style="font-size: 12px;height: 100%;line-height: 20px; vertical-align: middle; font-weight: normal;" class="deviceID">' + deviceId + '</td>' +
					'<td style="font-size: 10px;height: 100%;line-height: 20px; vertical-align: middle; font-weight: normal;" class="StartAt">' + formattedStartAt + '</td>' +
	          		'<td style="font-size: 12px;height: 100%;line-height: 20px; vertical-align: middle; font-weight: normal;" class="numMsg">' + numMsg + '</td>' +
	          		'<td style="font-size: 12px;height: 100%;line-height: 20px; vertical-align: middle; font-weight: normal;" class="rate">' + rate +'</td>' +
	          		'<td style="font-size: 10px;height: 100%;line-height: 20px; vertical-align: middle; font-weight: normal;" class="LastAt">' + formattedLastAt +'</td>' +
	          		'<td style="font-size: 12px;height: 100%;line-height: 20px; vertical-align: middle; font-weight: normal;" class="coords">' + coords + '</td>' +
	          		'<td style="font-size: 10px;height: 100%;line-height: 20px; vertical-align: middle; font-weight: normal;" class="LastDeviceTime">' + formattedLastDeviceTime + '</td>' + 
	          		'<td style="font-size: 10px;height: 100%;line-height: 20px; vertical-align: middle; font-weight: normal;" class="Delay">' + delay + '</td>' +
	          		'<td style="font-size: 10px;height: 100%;line-height: 20px; vertical-align: middle; font-weight: normal;" class="Speed">' + speed + '</td>' +
	          	 '</tr>';
	 	
	   $(newRow).appendTo(devicesTable.children('tbody'));
}


/**************************************************************
 *	Build a new Device : Device is internal struct
 */

function DeviceFactory ()
{
	this.buildDevice = function(jData)  {
		return new Device(jData);
	}

}

/**************************************************************
 *	Device is internal struct
 */

function Device(jData) { // internal scope
	
	// properties
	this.jData = jData; //
	this.startAt = new Date();
	this.lastAt = this.startAt; 
	this.speed = null;
	this.refreshRate = null;
	this.numMsg = 1;
	this.rate = 0;
	this.device = jData.device;
	
	// Adding a Layer and a Marker on the Layer
	
	//markerLayer = new OpenLayers.Layer.Markers("Devices");
	//this.layer = new OpenLayers.Layer.Markers(this.device); 
	//map.addLayer(markerLayer);
	
	this.position = new OpenLayers.LonLat(jData.data.coords.lon,jData.data.coords.lat).transform(projLonLat, projMercator);
	this.marker = new OpenLayers.Marker(this.position);
		
	markerLayer.addMarker(this.marker);
	
	//

	//***** Adding a row in table device startAt, lastAt
	addTableRow(this.jData, this.numMsg, this.rate, this.startAt, this.lastAt, this.delay, this.speed);
	

	//***** Use this function to update internal data of Device
	
	
	this.updateData = function(jData) {

		var oldData = this.jData;
		this.jData = jData;
		this.speed = 1;
		this.numMsg++;
		this.position = new OpenLayers.LonLat(jData.data.coords.lon,jData.data.coords.lat).transform(projLonLat, projMercator);
		
		
		
		this.lastAt = new Date();
		
		this.delay = (this.lastAt - new Date(this.jData.data.ts))/1000; //
		
		var num = this.numMsg/(this.lastAt - this.startAt)*Math.pow(10,3); //ms
		
		this.rate = Math.round(num*Math.pow(10,2))/Math.pow(10,2);
		
		// Math.round(price*Math.pow(10,2))/Math.pow(10,2);
		//var dist = Math.sqrt((oldData.lat - jData.lat) * (oldData.lat - jData.lat) + (oldData.lon - jData.lon) * (oldData.lon - jData.lon));
		//var elaps = jData.ts - oldTime.ts;
		//this.speed = dist / elaps;
		
		//  Move the Marker on the Layer
		var newPx = map.getLayerPxFromViewPortPx(map.getPixelFromLonLat(this.position));
		this.marker.moveTo(newPx);
		
		// table info
		updateTableRow(this.jData, this.numMsg, this.rate, this.startAt, this.lastAt, this.delay, this.speed);
		
	}
	
	
	
}
	




function setTopic(topic){	
	//$('table#devices > tbody').empty();
	socket.emit('subscribe',{topic: topic});
}




