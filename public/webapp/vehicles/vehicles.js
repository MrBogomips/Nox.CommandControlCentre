steal(
	function($){

		/**
		 * @class Webapp.vehicles
		 */
		Webapp.BaseForm('Webapp.vehicles',
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
				
				var renderForm = function() {
						(function() {
							self.element.html(jsRoutes.controllers.Assets.at("webapp/vehicles/views/default.ejs").url, self.options, function(el) {
								self.element.find(".switch").bootstrapSwitch();
								var el = self.element;
								$el = $(el);
								$el.modal('show');
								$el.on('hidden', function(){
									self.element.html('');
									self.destroy();
								});
							});
						})();
					},
					fetchVehicleInfo = function () {
						(function() {
							if (parseInt(self.options["id"]) > 0) {
								self.options.serverController.get(self.options["id"]).ajax({
									headers: { 
								        Accept : "application/json; charset=utf-8",
								        "Content-Type": "application/json; charset=utf-8"
								    },
									success: function(data) {
										$.extend(self.options, data);
										renderForm();
									}
								});	
							} else {
								renderForm();
							}
						})();
					};
				
				fetchVehicleInfo();
			}
		});

});