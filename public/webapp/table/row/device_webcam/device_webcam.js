steal('./css/device_webcam.css',
	  '/assets/webapp/webcam/webcam.js')
.then(
	function($){

		/**
		 * @class Webapp.device_webcam
		 */
		Aria.Controller('Webapp.device_webcam',
		/** @Static */
		{
			defaults : {
				'deviceName' : '',
				'webcams' : []
			}
		},
		
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('device_webcam');
				var width = self.options.webcams.length == 1 ? 528 : 263;
				var height = self.options.webcams.length == 1 ? 396 : 197;
				$.extend(self.options, { 'width' : width });
				$.extend(self.options, { 'height' : height });

				this.element.html('/assets/webapp/table/row/device_webcam/views/device_webcam.ejs', self.options, function(el) {
					var el = self.element.find('.modal.info');
					$el = $(el);
					$el.modal();
					$el.on('hidden', function(){
						self.element.html('');
						self.destroy();
					});
				});
			},
			
			destroy : function(){
				var self = this;
			    this._super();
			}
			
		});

});