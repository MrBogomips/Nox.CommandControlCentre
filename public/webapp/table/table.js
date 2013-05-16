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
				this.WS_Channel = Aria.Page.getInstance().getChannelByName("WS_MQTT");
				this.WS_Channel.subscribe('new_data', this.proxy(self._updateContent));
				
				// monitora gli eventi dei modelli
				webapp.models.device.bind('created', function(ev, device) {
					self._newDeviceFound(device);
				});
				
				this.element.html('/assets/webapp/table/views/table.ejs', {})
			} ,
			
			_newDeviceFound : function(device) {
				console.log('New device arrived ' + device);
			} ,
			
			_updateContent : function(event, data) {
				//$("#counter").html(parseInt($("#counter").html()) + 1);
				this._addRow(data);
				//console.log(data);
			},

			_callView : function() {
				var that = this;
				/*
				var r = this.element.find("tbody").append("<tr></tr>");
				r.webapp_row({});
				*/
			} ,

			_addRows : function(values) {
				var that = this;
				for (var i = 0; i < values.length; i++) {
					that._addRow(values[i]);	
				}
			} ,

			_addRow : function(data) {
					var row = this.element.find("[data-device-id='" + data.device +"']")[0];
					if (row) {
						$(row).controller().updateData(data);
					} else if (data.device){
						$('<tr data-device-id="'+data.device+'"></tr>')
							.appendTo(this.element.find('tbody'))
							.webapp_row(data);
					}
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