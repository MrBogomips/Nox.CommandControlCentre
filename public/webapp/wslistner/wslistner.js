steal('/assets/webapp/models/device.js')
	.then( function($){
		/**
		 * @class Webapp.channels
		 */
		Aria.Controller('webapp.wslistner',
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
				var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
				//var chatSocket = new WS("@routes.Application.chat(username).webSocketURL()")
				
				var initializeClientChannels = function () {
					self._onNewSubscription(null, {topic: self.app.configuration.eventClientTopic}, true);
					self._onNewSubscription(null, {topic: self.app.configuration.eventApplicationTopic}, true);
					self._onNewSubscription(null, {topic: self.app.configuration.eventUserTopic}, true);
					self._onNewSubscription(null, {topic: self.app.configuration.eventSessionTopic}, true);
				}
				
				self.socket = new WS(jsRoutes.controllers.Events.channel().webSocketURL());  // events_ws_uri
				
				self.socket.onmessage = $.proxy(self._onNewMsg, self);
				self.socket.onopen = function () {
					console.log("websocket open");
					initializeClientChannels();
				};
				self.socket.onclose = function () {
					console.log("websocket close");
				}
				
				self.WS_Channel = self.app.getChannelByName("WS_EVENTS");
				self.WS_Channel.subscribe("new_topic", self.proxy(self._onNewSubscription));
				
			},
			
			_onNewMsg: function(msg) {
				try {
			        var payload = $.parseJSON(msg.data);
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
				var prepareJson= function(command, topics) {
					var obj = {
						command: command,
						topics: [topics]
					};
					return JSON.stringify(obj);
				}
				if (noFunctionalChannels) {
					self.socket.send(prepareJson("subscribe", data.topic));
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
								self.socket.send(prepareJson("subscribe", t));
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