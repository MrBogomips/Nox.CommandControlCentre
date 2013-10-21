steal(function($){

		/**
		 * @class Webapp.table
		 */
		Aria.Controller('Webapp.lastdevice_info',
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
				this.element.addClass('webapp_lastdevice_info');
				
				// ascolta sul canale degli eventi direttamente
				this.TrackingChannel = Aria.Page.getInstance().getChannelByName("tracking");
				//this.MapChannel = Aria.Page.getInstance().getChannelByName("map");
				
				this.TrackingChannel.subscribe('position info', this.proxy(self._updateInfo));
				//this.TrackingChannel.subscribe('commandRequest commandResponse', this.proxy(self._updateCommandStatus));
								
				this.element.html('/assets/webapp/lastdevice_info/views/show.ejs', {})
			} ,
			
			destroy : function(){
				var self = this;
				this.TrackingChannel.unsubscribe('position info', this.proxy(self._updateInfo));
				//this.TrackingChannel.unsubscribe('commandRequest commandResponse', this.proxy(self._updateCommandStatus));
			    this._super();
			},
			
			_newDeviceFound : function(device) {
				console.log('New device arrived ' + device);
			} ,
			
			_updateInfo : function(event, data) {				
				var device = data.device;
				var ts = data.data.ts;
				
				this.element.find("span.device_name").html(device);
				this.element.find("span.timestamp").html(ts);
				/*
				var self = this;
				
				var row = this.element.find("[data-device-id='" + data.device + "']")[0];
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
					// Andrea Ciardi - valore di default
					if (data.device == 'dev_0') {
						data.webcams = [ { 'id' : 1 , 'file' : 'rtmp://nox02.prod.nexusat.it/myapp?carg=1/mystream1?sarg=2' } , { 'id' : 2 , 'file' : 'rtmp://nox02.prod.nexusat.it/myapp?carg=1/mystream2?sarg=2' } , { 'id' : 3 , 'file' : 'rtmp://nox02.prod.nexusat.it/myapp?carg=1/mystream3?sarg=2' } , { 'id' : 4 , 'file' : 'rtmp://nox02.prod.nexusat.it/myapp?carg=1/mystream4?sarg=2' } ];
					}
					else {
						data.webcams = [ { 'id' : 1 , 'file' : 'rtmp://nox02.prod.nexusat.it/myapp?carg=1/mystream3?sarg=2' } ];
					}

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
							$('<tr data-device-id="' + data.device + '"></tr>').appendTo(self.element.find('tbody')).webapp_row(data);
							self._notDiscard(discard, data);
						}
					);

				}
				*/
			}


		});

});