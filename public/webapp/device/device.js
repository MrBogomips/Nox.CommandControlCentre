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
				this.element.addClass('webapp_device');
				
				var renderForm = function() {
							self.element.html(jsRoutes.controllers.Assets.at("webapp/device/views/default.ejs").url, self.options, function(el) {
								var el = self.element.find(".modal");
								$el = $(el);
								$el.modal('show');
								$el.on('hidden', function(){
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
					    }})).done(function(dt, dg, di) {
					    	$.extend(self.options, {"types": dt[0]});
					    	$.extend(self.options, {"groups": dg[0]});
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
					    }})).done(function(dt, dg) {
					    	$.extend(self.options, {"types": dt[0]});
					    	$.extend(self.options, {"groups": dg[0]});
					    	renderForm();
					    });
				}
			} ,

			destroy : function(){
				var self = this;
			    this._super();
			},
			
			'.btn.device-create click' : function(el, ev) {
				var self = this;
				self._create(self.options.serverController);
			},

			'.btn.device-update click' : function(el, ev) {
				var self = this;
				self._update(self.options.serverController);
			}
		});

});