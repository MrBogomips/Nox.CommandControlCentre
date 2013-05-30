steal( '/assets/webapp/table/row/device_info/device_info.js',
	   '/assets/webapp/table/row/device_settings/device_settings.js',
	   '/assets/webapp/table/row/device_commandlog/device_commandlog.js',
	function($){

		/**
		 * @class Webapp.row
		 */
		Aria.Controller('Webapp.row',
		/** @Static */
		{
			defaults : {
				id : '',
				values : ''
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('webapp_table_row');
				this.data = this.options;
				this.commandQueue = {txs: {}, device: this.data.device};
				this.element.html('/assets/webapp/table/row/views/row2.ejs', self.options );
			} ,

			'.tool mousein' : function(el, ev) {
				$(el).tooltip('show');
			} ,

			'.tool mouseout' : function(el, ev) {
				$(el).tooltip('hide');
			} ,

			'.btn.settings click' : function(el, ev) {
				var ai = this.element.find('.anchorInfo'); 
				ai.webapp_device_settings(this.data);
			} ,
			
			'.btn.more click' : function(el, ev) {
				var ai = this.element.find('.anchorInfo'); 
				ai.webapp_device_info(this.data);
			} ,
			
			'.label.event-command-counter click' : function(el, ev) {
				var commandQueue = $(el).parent().parent().parent().controller().commandQueue;
				commandQueue.device = this.data.device;
				var ai = this.element.find('.anchorInfo'); 
				ai.webapp_device_commandlog(commandQueue);
			} ,

			'#tblChannelSettings .btn.unsubscribe click' : function(el, ev) {
				var that = this;
				var channel = $.trim($(el).closest('tr').find('td.string').html());
				var channels = $('#channels').controller().channels;
				for (var i = 0; i < channels.length; i++) {
					if (channels[i].value == channel) {
						$('#channels').controller().channels[i].subscribed = false;
						$(el).closest('tr').remove();
					}
				}
			},
			// Check if the passed data is older than the current
			checkOldData : function(data) {
				return this.data.data.ts > data.data.ts;
			},
			
			updateData : function(data) {
				var data_type = data.message_subtype
				switch (data_type) {
				case "info":
					this.data = data;
					var e = $(this.element.find(".counter:eq(0)"));
					e.html(parseInt(e.html()) + 1);
					// ignition
					var e = $(this.element.find(".ignition:eq(0)"));
					if (data.data.objs.ignition == 1) {
						e.html("on")
						 .addClass("label-success")
						 .removeClass("label-important");
					} else {
						e.html("off")
						 .addClass("label-important")
						 .removeClass("label-success");
					}
					// moving
					var e = $(this.element.find(".moving:eq(0)"));
					if (data.data.objs.ignition == 1) {
						e.html("moving")
						 .addClass("label-success")
						 .removeClass("label-important");
					} else {
						e.html("stop")
						 .addClass("label-important")
						 .removeClass("label-success");
					}
					// speed
					var e = $(this.element.find(".speed:eq(0)"));
					e.html(data.data.speed+"Km/h");
					break;
				case "position":
					alert("controller row: TODO: update position data only");
					break;
				case "commandRequest":
				case "commandResponse":
					var e = $(this.element.find(".event-command-counter:eq(0)"));
					if (e.html() == "0/0") e.css("visibility", "visible");
					var cs = e.html().split("/");
					if (this.commandQueue.txs[data.tranId] === undefined)
						this.commandQueue.txs[data.tranId] = {request: null, responses: []};
					if (data_type == "commandRequest") {
						cs[0] = parseInt(cs[0]) + 1;
						this.commandQueue.txs[data.tranId].request = data;
					}
					else {
						cs[1] = parseInt(cs[1]) + 1;
						this.commandQueue.txs[data.tranId].responses.push(data);
						if (data.exitstatus == "FAILED")
							e.addClass("label-important");
					}
					e.html(cs.join("/"));
					break;
				}
			}

		});

});