steal(
	function($){

		/**
		 * @class Webapp.table
		 */
		Webapp.BaseForm('Webapp.device',
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
				vehicle_id: null,
				creation_time: '',
				enabled: true,
				modification_time: '',
				serverController: jsRoutes.controllers.Device
			}
		},
		/** @Prototype */
		{
			
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('webapp_device modal hide fade');
				
				var renderForm = function() {
					self.element.html(jsRoutes.controllers.Assets.at("webapp/device/views/default.ejs").url, self.options, function(el) {
						self.element.find(".selectpicker").selectpicker();
						self.element.find(".switch").bootstrapSwitch();
						
						self.element.modal('show');
						self.element.on('hidden', function(){
							self.element.html('');
							self.destroy();
						});
					});
				};
				
				if (parseInt(self.options["id"]) > 0) {
					$.when(jsRoutes.controllers.DeviceType.index().ajax({
						headers: { 
					        Accept : "application/json; charset=utf-8",
					        "Content-Type": "application/json; charset=utf-8"
					    }}),
					    jsRoutes.controllers.DeviceGroup.index().ajax({
						headers: { 
					        Accept : "application/json; charset=utf-8",
					        "Content-Type": "application/json; charset=utf-8"
					    }}),
					    jsRoutes.controllers.Device.get(self.options["id"]).ajax({
						headers: { 
					        Accept : "application/json; charset=utf-8",
					        "Content-Type": "application/json; charset=utf-8"
					    }}),
					    jsRoutes.controllers.Vehicle.index().ajax({
							headers: { 
						        Accept : "application/json; charset=utf-8",
						        "Content-Type": "application/json; charset=utf-8"
						    }})
						).done(function(dt, dg, di, v) {
					    	$.extend(self.options, {"types": dt[0]});
					    	$.extend(self.options, {"groups": dg[0]});
					    	$.extend(self.options, {"vehicles": v[0]});
					    	$.extend(self.options, di[0]);
					    	renderForm();
					    });
				} else {
					$.when(jsRoutes.controllers.DeviceType.index().ajax({
						headers: { 
					        Accept : "application/json; charset=utf-8",
					        "Content-Type": "application/json; charset=utf-8"
					    }}),
					    jsRoutes.controllers.DeviceGroup.index().ajax({
						headers: { 
					        Accept : "application/json; charset=utf-8",
					        "Content-Type": "application/json; charset=utf-8"
					    }}),
					    jsRoutes.controllers.Vehicle.index().ajax({
							headers: { 
						        Accept : "application/json; charset=utf-8",
						        "Content-Type": "application/json; charset=utf-8"
						    }})
					    ).done(function(dt, dg, v) {
					    	$.extend(self.options, {"types": dt[0]});
					    	$.extend(self.options, {"groups": dg[0]});
					    	$.extend(self.options, {"vehicles": v[0]});
					    	renderForm();
					    });
				}
			}
		});

});