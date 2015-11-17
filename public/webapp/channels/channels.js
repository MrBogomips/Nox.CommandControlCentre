steal( '/assets/webapp/channels/views/channels.ejs', 
	   './css/channels.less', 
	   '/assets/webapp/models/channels.js',
	function($){
		/**
		 * @class Webapp.channels
		 */
		Aria.Controller('webapp.channels',
		/** @Static */
		{
			defaults : {
				model : webapp.models.channels
			}
		},
		/** @Prototype */
		{
			init : function() {
				var that = this;
				this._super();
				
				this.element.addClass('webapp_channels');
				
				this.WS_Channel = Aria.Page.getInstance().getChannelByName("WS_EVENTS");

				that.channelsList = '';
				jsRoutes.controllers.Channel.logisticIndex().ajax({
					headers: { 
				        "Accept" : "application/json; charset=utf-8",
				        "Content-Type" : "application/json; charset=utf-8"
				    }}
				).done(
					function(result) {
						//that.channelsList = '[';
						that.channels = [];
						for (var i = 0; i < result.channels.length; i++) {
							that.channels[i] = { 'value' : result.channels[i] ,'label' : result.channels[i], 'subscribed' : false , 'events' : false };
							//that.channelsList += '"' + result.channels[i] + '"';
							//if (i < (result.channels.length - 1)) {
								//that.channelsList += ',';
							//}
						}
						//that.channelsList += ']';
					}
				).fail(
					function (jqXHR, textStatus) {
						//that.channelsList = '';
					}
				).always(
					function() {
						that.element.html('/assets/webapp/channels/views/channels.ejs',{});
						var inputElement = $(".channel_name");
						inputElement.autocomplete({
							source: that.channels,
							minLength: 0,
							select: function( event, ui ){
								that._subscribe_channel(ui.item.label, false);
							}
						});
					}
				);
			} ,

			_call : function() {

			} ,

			_subscribe_channel : function(name, all_events) {
				
				this.WS_Channel.trigger("new_topic", {topic: name});
				
				this._call();
				//alert("You've subscribed to " + name);
			},
			
			'#testSubmit submit' : function(event) {
				var ch_name = this.element.find("input.channel_name").val();
				this._subscribe_channel(ch_name, false);
			},

			'.subscribeOne click' : function(el, ev) {
				var ch_name = this.element.find("input.channel_name").val();
				this._subscribe_channel(ch_name, false);
			} ,

			'.subscribeAll click' : function(el, ev) {
				var ch_name = this.element.find("input.channel_name").val();
				this._subscribe_channel(ch_name, true);
			}

		});
});