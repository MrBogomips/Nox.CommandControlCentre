steal( '/assets/aria/steal/less/less',
	   '/assets/aria/aria/controller/controller',
	   '/assets/aria/jquery/view/ejs/ejs')
.then( '/assets/webapp/settings/views/settings.ejs', 
	   './css/settings.less', 
	   '/assets/js/bootstrap.js', 
	   '/assets/css/bootstrap.css', 
	   '/assets/css/bootstrap-responsive.css',
	   '/assets/webapp/modal/modal.js',
	function($){

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
			init : function(){
				var that = this;
				this._super();

				this.element.addClass('webapp_settings');

				this.element.html('/assets/webapp/settings/views/settings.ejs',{});

				if ($('#anchorModalSettings').length <= 0) {
					$('body').prepend('<div id="anchorModalSettings"></div>');
				}
				$('#anchorModalSettings').webapp_modal({ 'id' : 'ChannelSettings' , 
														 'title' : 'Subscribed channels' , 
														 'plug' : 'webapp_table' , 
														 'content' : { 'type' : 'table' , 
														 			   'model' : webapp.models.channels , 
														 			   'labels' : [ 'Channel' , 'All events' , 'Actions' ] , 
														 			   'types' : [ 'string' , 'checkbox' , 'unsubscribe' ] ,
														 			   'values' : []
														 			 } 
													   });

			} ,

			'#channelSetting mousedown' : function(el, ev) {
				$("#modChannelSettings").modal('show');
			} ,

			'#modChannelSettings .close mousedown' : function(el, ev) {
				$("#modChannelSettings").modal('hide');
			}

		});

});