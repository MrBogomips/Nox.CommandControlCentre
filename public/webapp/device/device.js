steal(
	function($){

		/**
		 * @class Webapp.table
		 */
		Aria.Controller('Webapp.device',
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
				this.element.addClass('webapp_device');
				
				var renderForm = function() {
						(function() {
							self.element.html(jsRoutes.controllers.Assets.at("webapp/device/views/default.ejs").url, self.options, function(el) {
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
								jsRoutes.controllers.Device.get(self.options["id"]).ajax({
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
					},
					fecthDeviceGroups = function () {
						(function () {
							jsRoutes.controllers.DeviceGroup.index().ajax({
								headers: { 
							        Accept : "application/json; charset=utf-8",
							        "Content-Type": "application/json; charset=utf-8"
							    },
								success: function(data) {
									$.extend(self.options, {"groups": data});
									fetchDeviceInfo();
								}
							});
						})();
					},
					fetchDeviceTypes = function () {
						(function () {
							jsRoutes.controllers.DeviceType.index().ajax({
								headers: { 
							        Accept : "application/json; charset=utf-8",
							        "Content-Type": "application/json; charset=utf-8"
							    },
								success: function(data) {
									$.extend(self.options, {"types": data});
									fecthDeviceGroups();
								}
							});
						})();
					}; 
				
				fetchDeviceTypes();
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

				jsRoutes.controllers.Device.create().ajax({
					data: self.element.find('form').serialize(),
					success: function(data, txtStatus, jqXHR) {
						location = jsRoutes.controllers.Device.index().url;
					},
					error: self.proxy(self._reportError)
				});
			},
			".btn.device-update click": function(el, ev) {
				var self = this;

				jsRoutes.controllers.Device.update(self.options.id).ajax({
					data: self.element.find('form').serialize(),
					success: function(data, txtStatus, jqXHR) {
						location = jsRoutes.controllers.Device.index().url;
					},
					error: self.proxy(self._reportError)
				});
			}
		});

});