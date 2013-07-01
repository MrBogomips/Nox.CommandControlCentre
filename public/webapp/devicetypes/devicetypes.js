steal(
	function($){

		/**
		 * @class Webapp.devicetypes
		 */
		Aria.Controller('Webapp.devicetypes',
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
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('webapp_devicetypes');
				
				var renderForm = function() {
						(function() {
							self.element.html(jsRoutes.controllers.Assets.at("webapp/devicetypes/views/default.ejs").url, self.options, function(el) {
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
					fetchDeviceInfo = function () {
						(function() {
							if (parseInt(self.options["id"]) > 0) {
								jsRoutes.controllers.DeviceType.get(self.options["id"]).ajax({
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
				
				fetchDeviceInfo();
			} ,

			destroy : function(){
				var self = this;
			    this._super();
			},

			_reportError : function(data, txtStatus, jqXHR) {
				var $alert= $("<div class='alert alert-block alert-error'><button type='button' class='close' data-dismiss='alert'>×</button><h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p></div>");
				this.find(".alert_placeholder").html($alert);
			},
			
			".btn.device-create click": function(el, ev) {
				var self = this;

				jsRoutes.controllers.DeviceType.create().ajax({
					data: self.element.find('form').serialize(),
					success: function(data, txtStatus, jqXHR) {
						location = jsRoutes.controllers.DeviceType.index().url;
					},
					error: self.proxy(self._reportError)
				});
			},

			".btn.device-update click": function(el, ev) {
				var self = this;

				jsRoutes.controllers.DeviceType.update(self.options.id).ajax({
					data: self.element.find('form').serialize(),
					success: function(data, txtStatus, jqXHR) {
						location = jsRoutes.controllers.DeviceType.index().url;
					},
					error: self.proxy(self._reportError)
				});
			}
		});

});