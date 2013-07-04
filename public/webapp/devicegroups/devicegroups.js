steal(
	function($){

		/**
		 * @class Webapp.devicegroups
		 */
		Webapp.BaseForm('Webapp.devicegroups',
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
				this.element.addClass('webapp_devicegroups');
				
				var renderForm = function() {
						(function() {
							self.element.html(jsRoutes.controllers.Assets.at("webapp/devicegroups/views/default.ejs").url, self.options, function(el) {
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
								jsRoutes.controllers.DeviceGroup.get(self.options["id"]).ajax({
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

			".btn.device-create click": function(el, ev) {
				var self = this;

				jsRoutes.controllers.DeviceGroup.create().ajax({
					data: self.element.find('form').serialize(),
					success: function(data, txtStatus, jqXHR) {
						location = jsRoutes.controllers.DeviceGroup.index().url;
					},
					error: self.proxy(self._reportError)
				});
			},

			".btn.device-update click": function(el, ev) {
				var self = this;

				jsRoutes.controllers.DeviceGroup.update(self.options.id).ajax({
					data: self.element.find('form').serialize(),
					success: function(data, txtStatus, jqXHR) {
						location = jsRoutes.controllers.DeviceGroup.index().url;
					},
					error: self.proxy(self._reportError)
				});
			}
		});

});