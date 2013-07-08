steal(
	function($){

		/**
		 * @class Webapp.vehiclesassignements
		 */
		Webapp.BaseForm('Webapp.VehicleAssignements',
		/** @Static */
		{
			defaults : {
				idVehicle : -1 ,
				drivers : [] ,
				idDriver : -1 ,
				vehicles : [] ,
				assignements : [],
				serverControllerVehicle: jsRoutes.controllers.Vehicle,
				serverControllerDriver: jsRoutes.controllers.Driver,
				showDriver: true,
				showVehicle: true
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('webapp_vehiclesassignements');
				
				var renderForm = function() {
					var view = '';
					if ((self.options["idVehicle"] <= 0) && (self.options["idDriver"] <= 0)) {
						view = 'index';
					}
					else {
						view = 'modal';
					}
					
					self.element.html(jsRoutes.controllers.Assets.at("webapp/vehicleassignements/views/" + view + ".ejs").url, self.options, function(el) {
						// gestire la vista
					});
				};

				// List all the associations
				if ((self.options["idVehicle"] <= 0) && (self.options["idDriver"] <= 0)) {
					jsRoutes.controllers.VehicleAssignement.index().ajax({
						headers : { 
							'Accept' : 'application/json; charset=utf-8',
							'Content-Type' : 'application/json; charset=utf-8'
						}
					})
					.done(function(data, status, jqXHR) {
						$.extend(self.options["assignements"], data);
						renderForm();
					})
				}
			} ,

			

			destroy : function(){
				var self = this;
			    this._super();
			}
			
			
		});

}).then("/assets/webapp/vehicleassignements/row/row.js");