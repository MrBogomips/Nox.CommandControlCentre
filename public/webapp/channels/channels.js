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
				
				this.WS_Channel = Aria.Page.getInstance().getChannelByName("WS_MQTT");

				this.options.model.findAll(
					{  } , 
					function(d) {
						if (d.channels.length > 0) {
							that.channels = [];
							for (var i = 0; i < d.channels.length; i++) {
								that.channels[i] = { 'value' : d.channels[i] , 'subscribed' : false , 'events' : false };
							}
							that.element.html('/assets/webapp/channels/views/channels.ejs', { 'parent' : that , 'channels' : d.channels });
						}
					} ,
					function() { 
						alert('error'); 
					}
				);
			} ,

			_selectChannel : function(value) {
				this.element.find('input.channel_name').val(value);
				this.element.find('.inputChannels').attr('class', '');
			} ,

			_subcribe : function(all) {
				/*
				var valInput = this.element.find('input.channel_name').val();
				if (this.element.find('.ulChannels'))
					
					
					
				if ($('#ulChannels #' + valInput).length > 0) {
					for (var i = 0; i < this.channels.length; i++) {
						if (this.channels[i].value == valInput) {
							this.channels[i].subscribed = true;
							this.channels[i].events = (all == true ? true : false);
						}
					}
					this._call();
				}
				else {
					alert('This channel doesn\'t exist.');
				}
				*/
			} ,

			_call : function() {
				webapp.models.devices.findAll(
					{  } , 
					function(devices) {
						$('#devices').controller()._addRows(devices);
					} ,
					function() {
						alert('error');
					}
				);
			} ,

			_subscribe_channel: function(name, all_events) {
				this.element.find(".ulChannels").hide();
				
				
				this.WS_Channel.trigger("new_topic", {topic: name});
				
				this._call();
				//alert("You've subscribed to " + name);
			},
			
			
			_filterList : function(valInput) { // TODO: da riscrivere
				var arrItems = $('.ulChannels li');
				if (valInput != '') {
					var found = false;
					for (var i = 0; i < arrItems.length; i++) {
						if (($(arrItems[i]).attr('id') != 'error') && ($(arrItems[i]).attr('id') != 'noresults')) {
							if ($(arrItems[i]).attr('id').substring(0, valInput.length).toLowerCase() == valInput.toLowerCase()) {
								$(arrItems[i]).addClass('visible');
								found = true;
							}
							else {
								$(arrItems[i]).removeClass('visible');
							}
						}
						else {
							$(arrItems[i]).removeClass('visible');
						}
					}
					if (found == false) {
						$('.ulChannels #noresults').addClass('visible');
					}
				}
			} ,

			'input.channel_name focus' : function(el, ev) {
				this.element.find(".ulChannels").show();
				if (el.val() != '') {
					this._filterList(el.val());
					$('.inputChannels').attr('class', 'open');
				}
			} ,

			'input.channel_name blur' : function(el, ev) {
				this.element.find(".ulChannels").hide();
				$('.inputChannels').attr('class', '');
			} ,

			'.inputChannels input keyup' : function(el, ev) {
				if (el.val() != '') {
					this.element.find('.inputChannels').attr('class', 'open');
					this._filterList(el.val());
				}
				else {
					this.element.fin('.inputChannels').attr('class', '');
				}
			} ,

			'.subscribeOne click' : function(el, ev) {
				var ch_name = this.element.find("input.channel_name").val();
				this._subscribe_channel(ch_name, false);
			} ,

			'.subscribeAll click' : function(el, ev) {
				var ch_name = this.element.find("input.channel_name").val();
				this._subscribe_channel(ch_name, true);
			},
			
			'.channelItem a mousedown' : function(el, ev) {				
				this._selectChannel(el.attr("data-channel-id"));
			} 

		});
});