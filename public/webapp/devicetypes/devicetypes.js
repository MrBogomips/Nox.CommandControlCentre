steal(
	function($){

		/**
		 * @class Webapp.devicetypes
		 */
		Webapp.ModalForm('Webapp.devicetypes',
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
				version: -1,
				serverController: jsRoutes.controllers.DeviceType
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('devicetype');
				var view = jsRoutes.controllers.Assets.at("webapp/devicetypes/views/default.ejs").url;
				
				if (parseInt(self.options["id"]) > 0) {
					jsRoutes.controllers.DeviceType.get(self.options["id"]).ajax({
						headers: { 
					        Accept : "application/json; charset=utf-8",
					        "Content-Type": "application/json; charset=utf-8"
					    },
						success: function(data) {
							$.extend(self.options, data);
							self.renderForm(view);
						}
					});	
				} else {
					self.renderForm(view);
				}
			}
		});

});