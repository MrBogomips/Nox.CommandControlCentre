steal(
	function($){

		/**
		 * @class Webapp.Maintenanceservices
		 */
		Webapp.ModalForm('Webapp.Maintenanceservice',
		/** @Static */
		{
			defaults : {
				id : -1,
				name : '',
				displayName : '',
				description: '',
				odometer: '',
				monthsPeriod: '',
				enabled: true,
				creationTime : '',
				modificationTime : '',
				version : -1,
				serverController: jsRoutes.controllers.MaintenanceService
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('webapp_maintenanceservice modal hide fade');
				var view = jsRoutes.controllers.Assets.at("webapp/maintenanceservice/views/maintenanceservices.ejs").url;
				if (parseInt(self.options["id"]) > 0) {
					$.when(
						jsRoutes.controllers.MaintenanceService.get(self.options["id"]).ajax({
						headers: { 
					        Accept : "application/json; charset=utf-8",
					        "Content-Type": "application/json; charset=utf-8"
					    }})
					).done(function(dd) {
						$.extend(self.options, dd);
						self.renderForm(view);
					});
				} else {
					self.renderForm(view);
				}
			}
		});

});