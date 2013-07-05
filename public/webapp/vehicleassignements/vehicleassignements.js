steal(
	function($){

		/**
		 * @class Webapp.vehiclesassignements
		 */
		Webapp.BaseForm('Webapp.vehiclesassignements',
		/** @Static */
		{
			defaults : {
				idVehicle : -1 ,
				drivers : [] ,
				idDriver : -1 ,
				vehicles : [] ,
				serverControllerVehicle: jsRoutes.controllers.Vehicle,
				serverControllerDriver: jsRoutes.controllers.Driver
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
						view = 'vehicleassignements';
					}
					else {
						view = 'modal';
					}
					self.element.html(jsRoutes.controllers.Assets.at("webapp/vehicleassignements/views/" + view + ".ejs").url, self.options, function(el) {
						// gestire la vista
					});
				};

				$.when(
					self._getData()
				).then(
					function(){
						renderForm();
					}
				);
					
			} ,

			_getData : function() {
				var self = this;
				return $.Deferred(
					function(deferred){

						if ((self.options["idVehicle"] <= 0) && (self.options["idDriver"] <= 0)) {
							
							$.when(
								self._callIndex(serverControllerVehicle),
								self._callIndex(serverControllerDriver)
							).done(
								function(vehicles, drivers) {
									self.options["vehicles"] = vehicles[0];
									self.options["drivers"] = drivers[0];
									deferred.resolve();
								}
							);

						}
						else {
							if (self.options["idVehicle"] > 0) {
								$.when(
									// inserire chiamata
								).done(
									function(vehicles, drivers) {
										$.extend(self.options["drivers"], { 'find' : find , 'available' : available });
										deferred.resolve();
									}
								);
							}
							else {
								$.when(
									// inserire chiamata
								).done(
									function(find, available) {
										$.extend(self.options["vehicles"], { 'find' : find , 'available' : available });
										deferred.resolve();
									}
								);
							}
						}

					}
				).promise();
			},

			_callIndex : function(serverController) {
				serverController.index().ajax({
					headers : { 
						'Accept' : 'application/json; charset=utf-8',
						'Content-Type' : 'application/json; charset=utf-8'
					},
					success: function(data) {
						return data;
					}
				});
			},

			_callGet : function(serverController, id) {
				serverController.get(id).ajax({
					headers : { 
						'Accept' : 'application/json; charset=utf-8',
						'Content-Type' : 'application/json; charset=utf-8'
					},
					success: function(data) {
						return data;
					}
				});
			},

			destroy : function(){
				var self = this;
			    this._super();
			}
			
			
		});

});