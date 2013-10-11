steal('/assets/webapp/namedentity/namedentity.js',
	function($){

		/**
		 * @class Webapp.devicegroups
		 */
		Webapp.NamedEntity('Webapp.MaintenanceActivityOutcome',
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
				serverController: jsRoutes.controllers.MaintenanceActivityOutcome,
				className: 'maintenanceactivityoutcome',
				formTitle: 'maintenance activity outcome'
			}
		},
		/** @Prototype */
		{
			init : function() {
				this._super();
			}
		});

});