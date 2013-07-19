steal(
	function($){

		/**
		 * @class Webapp.vehicles
		 */
		Webapp.ModalForm('Webapp.vehicles',
		/** @Static */
		{
			defaults : {
				id : '',
				model : '',
				name : '',
				displayName : '',
				description: '',
				creationTime: '',
				model: '',
				enabled: true,
				modificationTime: '',
				version: -1,
				licensePlate: '',
				serverController: jsRoutes.controllers.Vehicle
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('webapp_vehicles modal hide fade');
				var view = jsRoutes.controllers.Assets.at("webapp/vehicles/views/default.ejs").url;
				
				if (parseInt(self.options["id"]) > 0) {
					self.options.serverController.get(self.options["id"]).ajax({
						headers: { 
					        Accept : "application/json; charset=utf-8",
					        "Content-Type": "application/json; charset=utf-8"
					    },
						success: function(data) {
							$.extend(self.options, data);
							self.renderForm(view);
						}
					});	
				} else {
					self.renderForm(view);
				}
			}
		});

});