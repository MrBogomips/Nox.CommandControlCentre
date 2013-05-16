steal(
	function($){

		/**
		 * @class Webapp.row
		 */
		Aria.Controller('Webapp.device_info',
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
				this.element.addClass('device_info');
				this.element.html('/assets/webapp/table/row/device_info/views/view.ejs', self.options, function(el) {
					var el = self.element.find('.modal.info');
					$el = $(el);
					$el.modal();
					$el.on('hidden', function(){
						self.element.html('');
						self.destroy();
					});
				});
			}
		});

});