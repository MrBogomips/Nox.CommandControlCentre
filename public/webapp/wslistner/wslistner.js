steal('/assets/js/socket.io.js', '/assets/webapp/models/device.js')
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
				self.socket = io.connect(Aria.Page.getInstance().configuration.eventsWebSocket);  // events_ws_uri
				
				var initializeClientChannels = function () {
					self._onNewSubscription(null, {topic: self.app.configuration.mqttClientTopic});
					self._onNewSubscription(null, {topic: self.app.configuration.mqttApplicationTopic});
					self._onNewSubscription(null, {topic: self.app.configuration.mqttUserTopic});
					self._onNewSubscription(null, {topic: self.app.configuration.mqttSessionTopic});
				}
			  
				self.socket.on('connect', function () {
					self.socket.on('mqtt', self.proxy(self._onNewMsg));
					console.log('socket.io::connect');
					initializeClientChannels();
			      });
			    
				self.WS_Channel = self.app.getChannelByName("WS_MQTT");
				self.WS_Channel.subscribe("new_topic", self.proxy(self._onNewSubscription));
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
			
			_onNewSubscription: function(event, data) {
				// TODO: manage logistic channels. For the moment we just subscribe to the POSITION
				this.socket.emit('subscribe',{topic: 'POSITION/'+data.topic});
			}
		});
});