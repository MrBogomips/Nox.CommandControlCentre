steal(
	function($){

		/**
		 * @class Webapp.vehiclesassignements
		 */
		Aria.Controller('Webapp.VehicleAssignements',
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
					var isModal = (self.options["idVehicle"] > 0) || (self.options["idDriver"] > 0);
					var content = $.View(jsRoutes.controllers.Assets.at("webapp/vehicleassignements/views/index.ejs").url, self.options);
					//isModal = true;
					if (isModal) {
						self.element.html(jsRoutes.controllers.Assets.at("webapp/vehicleassignements/views/modal.ejs").url, self.options, function(el){
							//$(el).find('.content').html(content);
							$(el).modal('hide');
							$(el).modal('show');
						});
					} else {
						self.element.html(content);
					}
				};
				
				var jsonReq = {
					headers : { 
						'Accept' : 'application/json; charset=utf-8',
						'Content-Type' : 'application/json; charset=utf-8'
					}	
				}
				
				var vehiclesIndex = jsRoutes.controllers.Vehicle.index();
				var driversIndex = jsRoutes.controllers.Driver.index();
				var assignementIndex = jsRoutes.controllers.VehicleAssignement.index();

				// List all the associations
				if ((self.options["idVehicle"] <= 0) && (self.options["idDriver"] <= 0)) {
					$.when(assignementIndex.ajax(jsonReq), vehiclesIndex.ajax(jsonReq), driversIndex.ajax(jsonReq))
					.done(function(ass, vehs, drvs) {
						$.extend(self.options["assignements"], ass[0]);
						$.extend(self.options["vehicles"], vehs[0]);
						$.extend(self.options["drivers"], drvs[0]);
						renderForm();
					})
				}
			},
			destroy : function(){
				var self = this;
			    this._super();
			},
			'.btn.create click' : function(el, ev) {
				if (el.hasClass('disabled')) return;
				var self = this;
				$tr=$("<tr></tr>")
				self.element.find('table').append($tr);
				$tr.webapp_vehicle_assignements_row(self.options);
				el.addClass('disabled')
			},
			'.btn.save-all click' : function(el, ev) {
				this.element.find('button.update[disabled!=disabled], button.save').click();
			},
			
			
		});

}).then("/assets/webapp/vehicleassignements/row/row.js");