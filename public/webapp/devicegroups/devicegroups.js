steal(
	function($){

		/**
		 * @class Webapp.devicegroups
		 */
		Webapp.ModalForm('Webapp.devicegroups',
		/** @Static */
		{
			defaults : {
				id : '',
				model : '',
				name : '',
				displayName : '',
				description: '',
				//type_id: '',
				//group_id: '',
				creationTime: '',
				enabled: true,
				modificationTime: '',
				version: -1,
				serverController: jsRoutes.controllers.DeviceGroup
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('devicegroup');
				var view = jsRoutes.controllers.Assets.at("webapp/devicegroups/views/default.ejs").url;
				
				if (parseInt(self.options["id"]) > 0) {
					jsRoutes.controllers.DeviceGroup.get(self.options["id"]).ajax({
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