steal('./css/device_webcam.css')
.then(
	function($){

		/**
		 * @class Webapp.device_webcam
		 */
		Aria.Controller('Webapp.device_webcam',
		/** @Static */
		{
			defaults : {
			}
		},
		
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('device_webcam');
				this.element.html('/assets/webapp/table/row/device_webcam/views/view.ejs', self.options, function(el) {
					var el = self.element.find('.modal.info');
					$el = $(el);
					$el.modal();
					$el.on('hidden', function(){
						self.element.html('');
						self.destroy();
					});
				});
				
			//	self.device = self.options.device;
				
				// ascolta sul canale degli eventi direttamente
			//	this.TrackingChannel = Aria.Page.getInstance().getChannelByName("tracking");
				
			//	this.TrackingChannel.subscribe('position info', this.proxy(self._updateInfo));
			},
			
			destroy : function(){
				var self = this;
			//	this.TrackingChannel.unsubscribe('info', this.proxy(self._updateInfo));
			//	this.TrackingChannel.unsubscribe('position', this.proxy(self._updateInfo));
			    this._super();
			}
			
		});

});