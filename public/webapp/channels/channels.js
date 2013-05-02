steal( '/assets/aria/steal/less/less',
	   '/assets/aria/aria/controller/controller',
	   '/assets/aria/jquery/view/ejs/ejs')
.then( '/assets/webapp/channels/views/channels.ejs', 
	   './css/channels.less', 
	   '/assets/js/bootstrap.js', 
	   '/assets/css/bootstrap.css', 
	   '/assets/css/bootstrap-responsive.css',
	   '/assets/webapp/models/channels.js',
	   '/assets/webapp/channels/items/items.js',
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

				this.options.model.findAll(
					{  } , 
					function(d) {
						if (d.channels.length > 0) {
							that.channels = [];
							for (var i = 0; i < d.channels.length; i++) {
								that.channels[i] = { 'value' : d.channels[i] , 'subscribed' : false , 'events' : false };
							}
							that.element.html('channels/views/channels.ejs', { 'parent' : that , 'channels' : d.channels });
						}
					} ,
					function() { 
						alert('error'); 
					}
				);
			} ,

			_selectChannel : function(value) {
				$('#inputChannels input').val(value);
				$('#inputChannels').attr('class', '');
			} ,

			_subcribe : function(all) {
				var valInput = $('#inputChannels input').val();
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

			_filterList : function(valInput) {
				var arrItems = $('#ulChannels li');
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
						$('#ulChannels #noresults').addClass('visible');
					}
				}
			} ,

			'#inputChannels input focus' : function(el, ev) {
				if (el.val() != '') {
					this._filterList($('#inputChannels input').val());
					$('#inputChannels').attr('class', 'open');
				}
			} ,

			'#inputChannels input blur' : function(el, ev) {
				$('#inputChannels').attr('class', '');
			} ,

			'#inputChannels input keyup' : function(el, ev) {
				if (el.val() != '') {
					$('#inputChannels').attr('class', 'open');
					this._filterList($('#inputChannels input').val());
				}
				else {
					$('#inputChannels').attr('class', '');
				}
			} ,

			'#subscribeOne click' : function(el, ev) {
				this._subcribe(false);
			} ,

			'#subscribeAll click' : function(el, ev) {
				this._subcribe(true);
			}

		});
		
		
});