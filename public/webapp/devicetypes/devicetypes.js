steal('/assets/webapp/namedentity/namedentity.js',
	function($){

		/**
		 * @class Webapp.devicetypes
		 */
		Webapp.NamedEntity('Webapp.devicetypes',
		/** @Static */
		{
			defaults : {
				id : '',
				model : '',
				name : '',
				displayName : '',
				description: '',
				creationTime: '',
				enabled: true,
				modificationTime: '',
				version: -1,
				serverController: jsRoutes.controllers.DeviceType,
				className: 'devicetypes',
				formTitle: 'device types'
			}
		},
		/** @Prototype */
		{
			init : function() {
				this._super();
			}
		});

});

/*
steal(
	function($){

		
		Webapp.ModalForm('Webapp.devicetypes',
		/ ** @Static * /
		{
			defaults : {
				id : '',
				model : '',
				name : '',
				displayName : '',
				description: '',
				creationTime: '',
				enabled: true,
				modificationTime: '',
				version: -1,
				serverController: jsRoutes.controllers.DeviceType
			}
		},
		/ ** @Prototype * /
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
*/