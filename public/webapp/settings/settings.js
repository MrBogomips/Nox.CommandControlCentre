steal( '/assets/webapp/settings/views/settings.ejs', 
	   './css/settings.less',
	function($) {

		/**
		 * @class Webapp.settings
		 */
		Aria.Controller('Webapp.settings',
		/** @Static */
		{
			defaults : {}
		},
		/** @Prototype */
		{
			init : function() {
				var that = this;
				this._super();

				this.element.addClass('webapp_settings');

				this.element.html('/assets/webapp/settings/views/settings.ejs',{});

				this.channelsSettings = [];

				if ($('#anchorModalSettings').length <= 0) {
					$('body').prepend('<div id="anchorModalSettings"></div>');
				}
				$('#anchorModalSettings').webapp_modal({ 'id' : 'ChannelSettings' , 
														 'title' : 'Subscribed channels' , 
														 'table' : { 'labels' : [ 'Channel' , 'All events' , 'Actions' ] , 
														 			 'values' : [  ] } 
													   });
				
			} ,

			'#channelSetting click' : function(el, ev) {
				var that = this;
				$('#modalBody').controller()._deleteRows();
				$('#modalBody').controller()._addRows(that._createRows());
				$("#modChannelSettings").modal('show');
			} ,

			_createRows : function() {
				var that = this;
				var channels = $('#channels').controller().channels;
				var x = 0;
				var values = [];
				for (var i = 0; i < channels.length; i++) {
					if (channels[i].subscribed == true) {
						values[x] = [ { 'type' : 'string' , 
										'description' : channels[i].value , 
										'editable' : false } ,
									  { 'type' : 'checkbox' , 
									  	'description' : channels[i].events , 
									  	'editable' : true , 
									  	'callback' : function(index) {
									  					$('#channels').controller().channels[index.index].events = ($('#channels').controller().channels[index.index].events == true ? false : true); 
									  				 } , 
									  	'parametres' : { 'index' : i } } ,
									  { 'type' : 'button' , 
									  	'description' : 'unsubscribe' , 
									  	'editable' : true } ];
						x++;
					}
				}
				return values;
			} ,

			/*_eventsChannelsSettings : function(channel) {
				for (var i = 0; i < this.channelsSettings.length; i++) {
					if (this.channelsSettings[i].value == channel) {
						return this.channelsSettings[i].events;
					}
				}
				return true;
			} ,*/

			'#modChannelSettings .close click' : function(el, ev) {
				$("#modChannelSettings").modal('hide');
			}

		});

});