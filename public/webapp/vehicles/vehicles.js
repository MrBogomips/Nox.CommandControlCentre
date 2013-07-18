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
				display_name : '',
				description: '',
				creation_time: '',
				vehicle_model: '',
				enabled: true,
				modification_time: '',
				version: -1,
				license_plate: '',
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