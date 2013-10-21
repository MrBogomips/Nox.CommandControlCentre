steal( '/assets/webapp/wshistorical_selector/views/show.ejs', 
	   './css/style.less', 
	function($){
		/**
		 * @class Webapp.channels
		 */
		Aria.Controller('webapp.wshistorical_selector',
		/** @Static */
		{
			defaults : {
				playback_socket: undefined
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				
				self.app = Aria.Page.getInstance();
				
				this.element.addClass('webapp_historical_selector');
				
				self.devicesarray = "";
				
				jsRoutes.controllers.Device.index(true).ajax({
					headers: { 
				        "Accept" : "application/json; charset=utf-8",
				        "Content-Type" : "application/json; charset=utf-8"
				    }
				}).done(function(result) {
					self.devicesarray = '[';
					for(var i = 0; i < result.length; ++i) {
						if (i > 0) self.devicesarray += ','; 
						self.devicesarray += '"' + result[i].name +'"';
					}
					self.devicesarray += ']';
				}).always(function() {
					self.element.html('/assets/webapp/wshistorical_selector/views/show.ejs', { devicesarray: self.devicesarray }, function() {
						self.element.find("#datetimepicker").datetimepicker({});
					});
				})
			} ,

			'.playback click' : function(el, ev) {
				var self = this;
				var hostname = location.host;
				var wsprotocol = location.protocol == "https:" ? "wss:" : "ws:";
				var dev_name = this.element.find("input.device_name").val();
				var start_time = this.element.find("input.date_time").val();
				
				var wsurl = jsRoutes.controllers.PositionPlayer.history(dev_name, start_time).absoluteURL().replace(location.protocol, wsprotocol);
				
				var opensocket = function () {
					console.log("web socket url: " + wsurl)
					self.playback_socket = new WebSocket(wsurl);
					self.playback_socket.onopen = function () {
						console.log("Web socket open")
					}
					self.playback_socket.onmessage = function (e) {
						//console.log(e.data)
						var payload = $.parseJSON(e.data);
						var channel = self.app.getChannelByName(payload.message_type);
						channel.trigger(payload.message_subtype, payload);
					}
				}
				
				if (typeof self.playback_socket != 'undefined') {
					try {
						self.playback_socket.onclose = function () {
							var f = function () {
								self.playback_socket = undefined;
								$("#devices").controller()._deleteRows();
								$("#map").controller()._removeAllMarkers();
								opensocket();
							}
							window.setTimeout(f, 1000);
						}
						self.playback_socket.close();
					} catch(err) {
						
					}
				} else {
					opensocket();
				}
				
				
			} ,

			'.subscribeAll click' : function(el, ev) {
				var ch_name = this.element.find("input.channel_name").val();
				this._subscribe_channel(ch_name, true);
			}

		});
});