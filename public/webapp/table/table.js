steal( '/assets/webapp/models/channels.js',
	   '/assets/webapp/models/device.js',
	   '/assets/webapp/table/row/row.js',
	function($){

		/**
		 * @class Webapp.table
		 */
		Aria.Controller('Webapp.table',
		/** @Static */
		{
			defaults : {
				id : '',
				model : '',
				labels : [],
				values : [],
				show_tools : true,
				show_webcam : true,
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('webapp_table');
				
				// ascolta sul canale degli eventi direttamente
				this.TrackingChannel = Aria.Page.getInstance().getChannelByName("tracking");
				this.MapChannel = Aria.Page.getInstance().getChannelByName("map");
				
				this.TrackingChannel.subscribe('position info', this.proxy(self._updateInfo));
				this.TrackingChannel.subscribe('commandRequest commandResponse', this.proxy(self._updateCommandStatus));
				
				// monitora gli eventi dei modelli
				//webapp.models.device.bind('created', function(ev, device) {
				//	self._newDeviceFound(device);
				//});
				
				this.element.html('/assets/webapp/table/views/table.ejs', {})
			} ,
			
			destroy : function(){
				var self = this;
				this.TrackingChannel.unsubscribe('position info', this.proxy(self._updateInfo));
				this.TrackingChannel.unsubscribe('commandRequest commandResponse', this.proxy(self._updateCommandStatus));
			    this._super();
			},
			
			_newDeviceFound : function(device) {
				console.log('New device arrived ' + device);
			} ,
			
			_updateInfo : function(event, data) {
				var self = this;
				var ele = this.element;
				var row = ele.find("[data-device-id='" + data.device + "']")[0];
				var discard = false;
				if (row) {
					if (Aria.Page.getInstance().configuration.eventsOutOfSequencePolicy == "DISCARD") {
						discard = $(row).controller().checkOldData(data);
					}
					if (!discard) {
						$(row).controller().updateData(data);
						self._notDiscard(discard, data);
					}
				} 
				else if (data.device) {
					var now = new Date();
					if (data.times == undefined) {
						data.times = { 'sessionstart' : now };
					}
					else {
						$.extend(data.times, { 'sessionstart' : now });
					}

					// PEZZO WEBCAM
					data.webcams = [ { 'id' : 1 , 'file' : 'rtmp://nox02.prod.nexusat.it/myapp?carg=1/mystream1?sarg=2' } , { 'id' : 2 , 'file' : 'rtmp://nox02.prod.nexusat.it/myapp?carg=1/mystream2?sarg=2' } , { 'id' : 3 , 'file' : 'rtmp://nox02.prod.nexusat.it/myapp?carg=1/mystream3?sarg=2' } , { 'id' : 4 , 'file' : 'rtmp://nox02.prod.nexusat.it/myapp?carg=1/mystream4?sarg=2' } ];

					$('<tr data-device-id="' + data.device + '"></tr>').appendTo(self.element.find('tbody'));//.webapp_row(data);

					jsRoutes.controllers.Device.getByName(data.device).ajax({
						headers: { 
					        Accept : "application/json; charset=utf-8",
					        "Content-Type": "application/json; charset=utf-8"
					    }}
					).done(
						function(di) {
							$.extend(data, { 'info' : di });
						}
					).fail(
						function (jqXHR, textStatus) {

						}
					).always(
						function() {
							$.extend(data, { 'show_tools' : self.options.show_tools, 'show_webcam' : self.options.show_webcam });
							self.element.find('tbody tr[data-device-id="' + data.device + '"]').webapp_row(data);
							//$('<tr data-device-id="' + data.device + '"></tr>').appendTo(self.element.find('tbody')).webapp_row(data);
							self._notDiscard(discard, data);
						}
					);

				}
			},

			_notDiscard : function(discard, data) {
				// prepare data for map {marker: "marker id", lat: <double>, lng: <double>, title: <text>}
				if (!discard) {
					var markerInfo = {
						marker: data.device,
						lat: data.data.coords.lat,
						lng: data.data.coords.lon,
						title: data.device
					};
					this.MapChannel.trigger("marker_position", markerInfo);
				}
			},

			_updateCommandStatus : function(event, data) {
				var row = this.element.find("[data-device-id='" + data.device +"']")[0];
				if (row) {
					$(row).controller().updateData(data);
				} else {
					console.log("WARNING: I've received a command request/response message for an unmonitored device [" + data.device + "]")
				}
			},

			_callView : function() {
				var that = this;
				/*
				var r = this.element.find("tbody").append("<tr></tr>");
				r.webapp_row({});
				*/
			} ,

			_deleteRows : function() {
				var arrRows = this.element.find('tbody tr');
				for (var i = 0; i < arrRows.length; i++) {
					if ($(arrRows[i]).hasClass('noresults') == false) {
						$(arrRows[i]).remove();
					}
				}
			},
			
			'#devices .updown click' : function(el, ev) {
				var rows = this.element.find(".table tbody");
				rows.toggle();
			}, 
			
			'#devices .leftright click' : function(el, ev) {
				var elem = this.element.find(".table .collapse");
				elem.toggle();
			} 

		});

});