steal(
	function($){

		/**
		 * @class Webapp.row
		 */
		Aria.Controller('Webapp.device_commandlog',
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
				this.element.addClass('device_commandlog');
				this.element.html('/assets/webapp/table/row/device_commandlog/views/view.ejs', self.options, function(el) {
					var el = self.element.find('.modal.log');
					$el = $(el);
					$el.modal();
					$el.on('hidden', function(){
						self.element.html('');
						self.destroy();
					});
				});
			}, 
			
			".btn.command click" : function (el, ev) {
				
			}
			
		});

});