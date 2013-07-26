steal(
	function($){

		/**
		 * @class Webapp.Menu
		 */
		Aria.Controller('Webapp.Menu',
		/** @Static */
		{
			defaults : {
				menu : []
			}
		},
		/** @Prototype */
		{
			init : function(options) {
				var self = this;
				self._super();
				self.element.addClass('webapp_menu');
				self.element.append(jsRoutes.controllers.Assets.at("webapp/menu/views/default.ejs").url, { 'menu' : self.options.menu }, function(el) {  })
			},

			destroy : function() {
				var self = this;
			    this._super();
			},

		});

});