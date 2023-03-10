steal('/assets/webapp/namedentity/namedentity.js',
	function($){

		/**
		 * @class Webapp.devicegroups
		 */
		Webapp.NamedEntity('Webapp.maintenanceactivityoutcome',
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
				className: 'MaintenanceActivityOutcome',
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