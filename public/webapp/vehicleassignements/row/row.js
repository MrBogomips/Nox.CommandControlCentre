steal(
	function($){

		/**
		 * @class Webapp.vehiclesassignements
		 */
		Webapp.BaseForm('Webapp.VehicleAssignements.Row',
		/** @Static */
		{
			defaults : {

			}
		},
		/** @Prototype */
		{
			init : function(options) {
				var self = this;
				this._super();
				this.element.addClass('webapp_vehiclesassignements_row');
				self.element.html("/assets/webapp/vehicleassignements/row/views/show.ejs", self.options);
				//self.element.html("BUCCHINO");
			} ,

			destroy : function(){
				var self = this;
			    this._super();
			}
			
			
		});

});