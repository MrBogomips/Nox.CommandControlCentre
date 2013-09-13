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
				typeId : -1,
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

					$.when(
						jsRoutes.controllers.Vehicle.get(self.options["id"]).ajax({
						headers: { 
					        Accept : "application/json; charset=utf-8",
					        "Content-Type": "application/json; charset=utf-8"
					    }}),
					    jsRoutes.controllers.DeviceGroup.index().ajax({
						headers: { 
					        Accept : "application/json; charset=utf-8",
					        "Content-Type": "application/json; charset=utf-8"
					    }})
					).done(function(dd, dt) {
						$.extend(self.options, dd[0]);
						$.extend(self.options, {"vehicleTypes": dt[0]});
						self.renderForm(view);
					});









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