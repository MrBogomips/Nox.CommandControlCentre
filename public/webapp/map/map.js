steal( '/assets/webapp/models/channels.js',
	   '/assets/webapp/models/device.js',
	   '/assets/webapp/table/row/row.js',
	   '/assets/leaflet/leaflet.js', 
   	'/assets/leaflet/leaflet.css')
.then(function($){

		/**
		 * @class Webapp.table
		 */
		Aria.Controller('Webapp.map',
		/** @Static */
		{
			defaults : {
				attributionControl: false,
				autoposition: true,			// if TRUE position the map to the current position of the device
				coords: {					// if autoposition == FALSE then position the map to the current position
					latitude: null,
					longitude: null			
				},
				cloudmadeAppKey: ""
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('map');
				
				
				//projMercator = new OpenLayers.Projection("EPSG:900913"); 
				
				
				
				var initMap = function (pos) {
	            	self.map = L.map('map', {
	            		attributionControl: self.options.attributionControl,
	            		crs: L.CRS.EPSG3857
	            		}
	            	).setView([pos.coords.latitude, pos.coords.longitude], 13);
	            	L.tileLayer('http://{s}.tile.cloudmade.com/'+self.options.cloudmadeAppKey+'/997/256/{z}/{x}/{y}.png', {
	    				attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery &copy; <a href="http://cloudmade.com">CloudMade</a>',
					    maxZoom: 18
					}).addTo(self.map);
				}
				
				if (self.options.autoposition)
					navigator.geolocation.getCurrentPosition(initMap);
				else {
					var pos = {coords: self.options.coords};
					initMap(pos);
				}
				
				// ascolta sul canale degli eventi direttamente
				//this.TrackingChannel = Aria.Page.getInstance().getChannelByName("tracking");
				
				// this.TrackingChannel.subscribe('position info', this.proxy(self._updateInfo));
				// this.TrackingChannel.subscribe('commandRequest commandResponse', this.proxy(self._updateCommandStatus)); 
				
				// monitora gli eventi dei modelli
				// webapp.models.device.bind('created', function(ev, device) {
				//	self._newDeviceFound(device);
				//});
				
				//this.element.html('/assets/webapp/table/views/table.ejs', {})
			}	
		});  // Aria-class
}); // steal