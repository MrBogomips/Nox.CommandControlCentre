steal( '/assets/webapp/models/channels.js',
	   '/assets/webapp/models/device.js',
	   '/assets/webapp/table/row/row.js',
	   '/assets/leaflet/leaflet.js', 
	   '/assets/leaflet/leaflet.css',
	   '/assets/leaflet/maps.google.js',
	   '/assets/leaflet/ymaps.js'
	   )
.then( '/assets/leaflet/Bing.js',
	   '/assets/leaflet/Google.js',
	   '/assets/leaflet/Yandex.js', function($){

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
	            	self.map = new L.map('map', {
	            		attributionControl: self.options.attributionControl,
	            		crs: L.CRS.EPSG3857
	            		}
	            	).setView([pos.coords.latitude, pos.coords.longitude], 13);
	            	
	            	/*L.TileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
	    				attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery &copy; <a href="http://cloudmade.com">CloudMade</a>',
					    maxZoom: 18
					}).addTo(self.map);*/
	            	
	            	var ggl = new L.Google('SATELLITE'); //Google leayer: [SATELLITE, ROADMAP, HYBRID, TERRAIN]
	            	var ggl2 = new L.Google('ROADMAP');
	            	var ggl3 = new L.Google('HYBRID');
	            	var ggl4 = new L.Google('TERRAIN'); 
	            	
	            	var bingCode = "LfO3DMI9S6GnXD7d0WGs~bq2DRVkmIAzSOFdodzZLvw~Arx8dclDxmZA0Y38tHIJlJfnMbGq5GXeYmrGOUIbS2VLFzRKCK0Yv_bAl6oe-DOc"; 
	                var bing1 = new L.BingLayer(bingCode, {type: "Aerial"});// AerialWithLabels | Birdseye | BirdseyeWithLabels | Road
	            	
	            	var osm = new L.TileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png');
	            	
	            	var yndx1 = new L.Yandex();
	            	var yndx2 = new L.Yandex("null", {traffic:true, opacity:0.8, overlay:true});
	            	
	            	self.map.addLayer(osm); //set default layer
	            	self.map.addControl(new L.Control.Layers( {
            			'Openstreetmap':osm, 
            			'Google1':ggl, 
            			'Google2':ggl2, 
            			'Google3':ggl3, 
            			'Google4':ggl4,
            			'Bing1':bing1,
            			'Yandex1':yndx1,
            			'Yandex2':yndx2
        			},{}));
	            	
	            	
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
				if (typeof (marker = this.markers[data.marker]) == 'undefined') {
					this.markers[data.marker] = L.marker([data.lat, data.lng], {title:data.title}).addTo(this.map).bindPopup(data.title);
				} else {
					marker.setLatLng([data.lat, data.lng]);
				}
			},
			
			_removeAllMarkers: function() {
				for (var k in this.markers) {
					this.map.removeLayer(this.markers[k])
				}
				this.markers = {}
			},

			'.leaflet-clickable mouseup' : function(el, ev) {
					var name = $(el).attr('title');
					var marker = this.markers[name];
					this.map.setView([marker._latlng.lat, marker._latlng.lng], 13);
			}
			
		});  // Aria-class
}); // steal