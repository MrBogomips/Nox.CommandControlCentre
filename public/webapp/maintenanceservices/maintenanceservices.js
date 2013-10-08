steal(
	function($){

		/**
		 * @class Webapp.Maintenanceservices
		 */
		Webapp.ModalForm('Webapp.Maintenanceservices',
		/** @Static */
		{
			defaults : {
				id : '',
				name : '',
				displayName : '',
				description: '',
				odometer: '',
				monthsPeriod: '',
				enabled: true,
				creationTime : '',
				modificationTime : '',
				varsion : -1
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('webapp_maintenanceservices modal hide fade');
				var view = jsRoutes.controllers.Assets.at("webapp/maintenanceservices/views/maintenanceservices.ejs").url;
				if (parseInt(self.options["id"]) > 0) {
					$.when(
						jsRoutes.controllers.MaintenanceService.get(self.options["id"]).ajax({
						headers: { 
					        Accept : "application/json; charset=utf-8",
					        "Content-Type": "application/json; charset=utf-8"
					    }})
					).done(function(dd) {
						$.extend(self.options, dd[0]);
						self.renderForm(view);
					});
				} else {
					self.renderForm(view);
				}
			}
		});

});