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
				values : []
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
			
			_newDeviceFound : function(device) {
				console.log('New device arrived ' + device);
			} ,
			
			_updateInfo : function(event, data) {
				//$("#counter").html(parseInt($("#counter").html()) + 1);
				var row = this.element.find("[data-device-id='" + data.device +"']")[0];
				if (row) {
					$(row).controller().updateData(data);
				} else if (data.device){
					$('<tr data-device-id="'+data.device+'"></tr>')
						.appendTo(this.element.find('tbody'))
						.webapp_row(data);
				}
				
				// prepare data for map {marker: "marker id", lat: <double>, lng: <double>, title: <text>}
				var markerInfo = {
					marker: data.device,
					lat: data.data.coords.lat,
					lng: data.data.coords.lon,
					title: data.device
				};
				this.MapChannel.trigger("marker_position", markerInfo);
			},
			
			_updateCommandStatus : function(event, data) {
				var row = this.element.find("[data-device-id='" + data.device +"']")[0];
				if (row) {
					$(row).controller().updateData(data);
				} else {
					alert("FATAL ERROR: I've received a command request/response message for an unmonitored device ["+data.device+"]")
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
			}

		});

});