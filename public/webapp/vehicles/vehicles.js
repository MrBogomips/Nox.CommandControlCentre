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
				type_id: '',
				group_id: '',
				creation_time: '',
				vehicle_model: '',
				enabled: true,
				modification_time: '',
				serverController: jsRoutes.controllers.Vehicle
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('webapp_vehicles');
				
				var renderForm = function() {
						(function() {
							self.element.html(jsRoutes.controllers.Assets.at("webapp/vehicles/views/default.ejs").url, self.options, function(el) {
								var el = self.element.find(".modal");
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
			} ,

			destroy : function(){
				var self = this;
			    this._super();
			},
			
			'.btn.vehicle-create click' : function(el, ev) {
				var self = this;
				self._create(self.options.serverController);
			},

			'.btn.vehicle-update click' : function(el, ev) {
				var self = this;
				self._update(self.options.serverController);
				
			}
		});

});