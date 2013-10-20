steal('/assets/webapp/models/device.js')
	.then( function($){
		/**
		 * @class Webapp.channels
		 */
		Aria.Controller('webapp.wshistorical',
		/** @Static */
		{
			defaults : { }
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this.app = Aria.Page.getInstance();
				this._super();
				
				devices = {};
				
				// History Playback PoC -- Just to show how it works
				if (false) {
					self.playback_socket = new WebSocket("ws://localhost:9000/positions/play_back?device=dev_0&start=2013-10-11T19%3A23%3A30");
					self.playback_socket.onopen = function () {
						console.log("Web socket open")
					}
					self.playback_socket.onmessage = function (e) {
						console.log(e.data)
						var payload = $.parseJSON(e.data);
						var channel = self.app.getChannelByName(payload.message_type);
						channel.trigger(payload.message_subtype, payload);
					}
				}
			},
			
			_onNewMsg: function(msg) {
				try {
			        var payload = $.parseJSON(msg.payload);
			        var channel = this.app.getChannelByName(payload.message_type);  // es.: tracking, chat, …
			        channel.trigger(payload.message_subtype, payload); // es.: position, info, beginchat, …
			        if (payload.message_subtype == "commandResponse") {
			        	console.log(payload);
			        }
				} catch(err) {
					console.log("Ws listner: [" + msg + "] with err [" + err.message + "] wasn't a valid JSON or wasn't in the expected format => Ignored");
				}
		        //console.log(jData);
			},
			
			_onNewSubscription: function(event, data, noFunctionalChannels) {
				// TODO: manage logistic channels. For the moment we just subscribe to the POSITION
				var self = this
				if (noFunctionalChannels) {
					self.socket.emit('subscribe',{topic: data.topic});
				} 
				else
				{
					jsRoutes.controllers.Channel.functionalIndex().ajax({
						headers: { 
					        "Accept" : "application/json; charset=utf-8",
					        "Content-Type" : "application/json; charset=utf-8"
					    }}
					).done(
						function(result) {
							for (var i = 0; i < result.channels.length; i++) {
								var t = result.channels[i] +'/'+data.topic;
								self.socket.emit('subscribe',{topic: t});
								console.log("Subscribed to the topic [" + t + "]")
							}
						}
					).fail(
						console.log("Error retrieving the list of the functional channels enabled")
					);
				}	
			}
		});
});