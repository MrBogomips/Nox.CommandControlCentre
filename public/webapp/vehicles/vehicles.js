steal(
	function($){

		/**
		 * @class Webapp.vehicles
		 */
		Aria.Controller('Webapp.vehicles',
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
								jsRoutes.controllers.Vehicle.get(self.options["id"]).ajax({
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

			_reportError : function(data, txtStatus, jqXHR) {
				var $alert= $("<div class='alert alert-block alert-error'><button type='button' class='close' data-dismiss='alert'>×</button><h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p></div>");
				this.find(".alert_placeholder").html($alert);
			},
			
			".btn.vehicle-create click": function(el, ev) {
				var self = this;

				jsRoutes.controllers.Vehicle.create().ajax({
					data: self.element.find('form').serialize(),
					success: function(data, txtStatus, jqXHR) {
						location = jsRoutes.controllers.Vehicle.index().url;
					},
					error: self.proxy(self._reportError)
				});
			},

			".btn.vehicle-update click": function(el, ev) {
				var self = this;

				jsRoutes.controllers.Vehicle.update(self.options.id).ajax({
					data: self.element.find('form').serialize(),
					success: function(data, txtStatus, jqXHR) {
						location = jsRoutes.controllers.Vehicle.index().url;
					},
					error: function() {
						self.proxy(self._reportError);
					}
				});
			}
		});

});