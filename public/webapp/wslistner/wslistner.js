steal('/assets/js/socket.io.js',
	function($){
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
				var that = this;
				this._super();
				
				//this.element.addClass('webapp_channels');

				alert("Hello world listner");

				//var devFact = new DeviceFactory();

				devices = {};
			    this.socket = io.connect("http://nox01.prod.nexusat.int:5000");  // events_ws_uri
			  
			    this.socket.on('connect', function () {
			        that.socket.on('mqtt', that.proxy(that._onNewMsg));
			      });
			    
			    this.WS_Channel = Aria.Page.getInstance().getChannelByName("WS_MQTT");
			    this.WS_Channel.subscribe("new_topic", that.proxy(that._onNewSubscription))
			},
			
			_onNewMsg: function(msg) {
				var jData = $.parseJSON(msg.payload);
		        var tokens = msg.topic.split("/");
		       
		        //The message type is the last one token in array
		        var messageType = tokens[0];
		        
		        this.WS_Channel.trigger("new_data", jData);
		        //console.log(jData);
		        /*
		  		var e;
		  		
		  		if((e = devices[jData.device]) == undefined)
		  		{
		  			// new device found!
		  			e = devices[jData.device]=devFact.buildDevice(messageType, jData);
		  		}
		  		else
		  		{
		  			e.updateData(messageType, jData);
		  		}
		  		*/
			},
			
			_onNewSubscription: function(event, data) {
				this.socket.emit('subscribe',{topic: data.topic});
			}
		});
});