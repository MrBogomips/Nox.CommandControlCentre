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

				jsRoutes.controllers.User.getCurrent().ajax({
						headers: { 
					        Accept : "application/json; charset=utf-8",
					        "Content-Type": "application/json; charset=utf-8"
					    },
						success: function(user) {
							user.photo = jsRoutes.controllers.Assets.at("img/defaultPhoto.png").url;
							$.extend(self.options, user);
							self.element.append(jsRoutes.controllers.Assets.at("webapp/menu/views/default.ejs").url, { 'menu' : self.options.menu , 'user' : user }, function(el) {  });
						}
					});	

			},

			destroy : function() {
				var self = this;
			    this._super();
			}

		});

});