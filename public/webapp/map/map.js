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
				cloudmadeAppKey: '',
				markerChannel: undefined	// An aria channel where to monitor the event "marker_position"
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('map');
				this.markers = {};
				
				//projMercator = new OpenLayers.Projection("EPSG:900913"); 
				
				
				
				var initMap = function (pos) {
	            	self.map = L.map('map', {
	            		attributionControl: self.options.attributionControl,
	            		crs: L.CRS.EPSG3857
	            		}
	            	).setView([pos.coords.latitude, pos.coords.longitude], 13);
	            	L.tileLayer('http://{s}.tile.cloudmade.com/' + self.options.cloudmadeAppKey + '/997/256/{z}/{x}/{y}.png', {
	    				attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery &copy; <a href="http://cloudmade.com">CloudMade</a>',
					    maxZoom: 18
					}).addTo(self.map);
	            	
	            	
	            	// DEMO MARKER
	            	//var marker = L.marker([pos.coords.latitude, pos.coords.longitude], {draggable:true}).addTo(self.map)
	            	
	            	
	            	if (self.options.markerChannel !== undefined) {
	            		self.options.markerChannel.subscribe("marker_position", self.proxy(self._updateMarker));
	            	}
	            	
				}
				
				if (self.options.autoposition)
					navigator.geolocation.getCurrentPosition(initMap);
				else {
					var pos = {coords: self.options.coords};
					initMap(pos);
				}
			},
			
			/*
			 * data is expected to be {marker: "marker id", lat: <double>, lng: <double>, title: <text>}
			 */
			_updateMarker: function(event, data) {
				var marker;
				if ((marker = this.markers[data.marker]) === undefined) {
					this.markers[data.marker] = L.marker([data.lat, data.lng], {title:data.title}).addTo(this.map).bindPopup(data.title);
				} else {
					marker.setLatLng([data.lat, data.lng]);
				}
			},

			'.leaflet-clickable mouseup' : function(el, ev) {
					var name = $(el).attr('title');
					var marker = this.markers[name];
					this.map.setView([marker._latlng.lat, marker._latlng.lng], 13);
			}
			
		});  // Aria-class
}); // steal