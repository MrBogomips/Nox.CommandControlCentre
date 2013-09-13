steal('/assets/leaflet/leaflet.js', 
   	  '/assets/leaflet/leaflet.css')
.then(
	function($){

		/**
		 * @class Webapp.row
		 */
		Aria.Controller('Webapp.device_info',
		/** @Static */
		{
			defaults : {
			}
		},
		
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('device_info');
					$.when(
						self.options.parent._getDeviceInfo(self.options)
					).then(
						function() {
							if (self.options.info == undefined) {
								self.options.info = null;
							}
							self.element.html('/assets/webapp/table/row/device_info/views/device_info.ejs', self.options, function(el) {
								var el = self.element.find('.modal.info');
								$el = $(el);
								$el.modal();
								$el.on('hidden', function(){
									self.element.html('');
									self.destroy();
								});


								var initMap = function (pos) {
					            	self.map = L.map('mini-map', {
					            		attributionControl: $('#map').controller().options.attributionControl,
					            		crs: L.CRS.EPSG3857
					            		}
					            	).setView([pos.coords.lat, pos.coords.lon], 13);
					            	L.tileLayer('http://{s}.tile.cloudmade.com/' + $('#map').controller().options.cloudmadeAppKey + '/997/256/{z}/{x}/{y}.png', {
					    				attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery &copy; <a href="http://cloudmade.com">CloudMade</a>',
									    maxZoom: 18
									}).addTo(self.map);
					            //	if (self.options.markerChannel !== undefined) {
					            //		self.options.markerChannel.subscribe("marker_position", self.proxy(self._updateMarker));
					            //	}
								}
								
								initMap(self.options.data);

							});
						
							self.device = self.options.device;
							
							// ascolta sul canale degli eventi direttamente
							self.TrackingChannel = Aria.Page.getInstance().getChannelByName("tracking");
							
							self.TrackingChannel.subscribe('position info', self.proxy(self._updateInfo));
						}
					)
			},
			
			destroy : function(){
				var self = this;
				this.TrackingChannel.unsubscribe('info', this.proxy(self._updateInfo));
				this.TrackingChannel.unsubscribe('position', this.proxy(self._updateInfo));
			    this._super();
			},
			
			_updateInfo : function(event, data) {
				if (this.device == data.device) {
					var base = this.element.find(".extended-info table tbody");
					var props = data.data.objs;
					
					for(var k in props) {
						var e = base.find("span." + k);
						e.html(data.data.objs[k]);
					}
					
				}
			}
		});

});