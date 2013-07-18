steal(function($) {

	/**
	 * @class Webapp.table
	 */
	Webapp.ModalForm('Webapp.Driver',
	/** @Static */
	{
		defaults : {
			id : '',
			name : '',
			surname : '',
			display_name : '',
			creation_time : '',
			modification_time : '',
			enabled : true,
			version: -1,
			serverController: jsRoutes.controllers.Driver
		}
	},
	/** @Prototype */
	{
		init : function() {
			var self = this;
			this._super();
			this.element.addClass('driver');
			var view = jsRoutes.controllers.Assets.at("webapp/driver/views/default.ejs").url;
			
			if (parseInt(self.options["id"]) > 0) {
				jsRoutes.controllers.Driver.get(self.options["id"]).ajax({
					headers : {
						Accept : "application/json; charset=utf-8",
						"Content-Type" : "application/json; charset=utf-8"
					}
				}).done(function(data, dg, di) {
					$.extend(self.options, data);
					self.renderForm(view);
				});
			} else {
				self.renderForm(view);
			}
		}
	});

});