steal(
	function($) {

		/**
		 * @class Webapp.Maintenanceoperators
		 */
		Webapp.ModalForm('Webapp.Operator',
		/** @Static */
		{
			defaults : {
				id : '',
				name : '',
				surname : '',
				displayName : '',
				enabled : true,
				creationTime : '',
				modificationTime : '',
				version: -1,
				serverController: jsRoutes.controllers.Operator
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('webapp_maintenanceoperator modal hide fade');
				var view = jsRoutes.controllers.Assets.at("webapp/operator/views/maintenanceoperators.ejs").url;
				
				if (parseInt(self.options["id"]) > 0) {
					jsRoutes.controllers.Operator.get(self.options["id"]).ajax({
						headers : {
							Accept : "application/json; charset=utf-8",
							"Content-Type" : "application/json; charset=utf-8"
						}
					}).done(function(dd) {
						$.extend(self.options, dd);
						self.renderForm(view);
					});
				} else {
					self.renderForm(view);
				}
			}
		});

});