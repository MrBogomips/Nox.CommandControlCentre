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

				self.WS_Channel = self.app.getChannelByName("WS_EVENTS");
				
				var initializeClientChannels = function () {
					self._onNewSubscription(null, {topic: self.app.configuration.eventClientTopic}, true);
					self._onNewSubscription(null, {topic: self.app.configuration.eventApplicationTopic}, true);
					self._onNewSubscription(null, {topic: self.app.configuration.eventUserTopic}, true);
					self._onNewSubscription(null, {topic: self.app.configuration.eventSessionTopic}, true);

					self.WS_Channel.subscribe("new_topic", self.proxy(self._onNewSubscription));
					self.WS_Channel.trigger("socket_ready", "");
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
					var ts = (function () {
						if (typeof topics === 'string') return [topics]
						else return topics
					})();
					var obj = {
						command: command,
						topics: ts
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
							/*
							var topics = [];
							for (var i = 0; i < result.channels.length; i++) {
								var t = result.channels[i] +'/'+data.topic;
								topics.push(t);
							}
							*/
							var topics = _.map(result.channels, function(c) {return c + '/'+data.topic;})
							self.socket.send(prepareJson("subscribe", topics));
							console.log("Subscribed to the topics [" + topics + "]")
						}
					).fail(
						console.log("Error retrieving the list of the functional channels enabled")
					);
				}	
			}
		});
});