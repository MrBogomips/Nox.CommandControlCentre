steal(
	function($){

		/**
		 * @class Webapp.table
		 */
		Webapp.ModalForm('Webapp.Simcard',
		/** @Static */
		{
			defaults : {
				id : '',
				imei : '',
				displayName : '',
				description: '',
				mobileNumber: '',
				carrierId: '',
				creationTime: '',
				modificationTime: '',
				version: -1,
				enabled: true,
				serverController: jsRoutes.controllers.Simcard
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('simcard');
				var view = jsRoutes.controllers.Assets.at("webapp/simcard/views/default.ejs").url;
				
				if (parseInt(self.options["id"]) > 0) {
					$.when(
					    jsRoutes.controllers.Simcard.get(self.options["id"]).ajax({
						headers: { 
					        Accept : "application/json; charset=utf-8",
					        "Content-Type": "application/json; charset=utf-8"
					    }})
						).done(function(si) {
					    	$.extend(self.options, si);
					    	self.renderForm(view);
					    });
				} else {
					self.renderForm(view);
				} 
			}
		});

});