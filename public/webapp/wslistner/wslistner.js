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
				var me = this;
				this._super();
				
				devices = {};
				me.socket = io.connect("http://nox01.prod.nexusat.int:5000");  // events_ws_uri
			  
				me.socket.on('connect', function () {
					me.socket.on('mqtt', me.proxy(me._onNewMsg));
			      });
			    
				me.WS_Channel = Aria.Page.getInstance().getChannelByName("WS_MQTT");
				me.WS_Channel.subscribe("new_topic", me.proxy(me._onNewSubscription))
			},
			
			_onNewMsg: function(msg) {
				var jData = $.parseJSON(msg.payload);
		        var tokens = msg.topic.split("/");
		       
		        //The message type is the last one token in array
		        var messageType = tokens[0];
		        
		        if (jData.device) {
		        	//var device = new webapp.models.device($.parseJSON(msg.payload));
		        	var device = new webapp.models.device(jData);
		        	//this.WS_Channel.trigger("new_data", jData);
		        	this.WS_Channel.trigger("new_data", device);
		        }
		        
		        
		        //console.log(jData);
			},
			
			_onNewSubscription: function(event, data) {
				this.socket.emit('subscribe',{topic: data.topic});
			}
		});
});