steal('/assets/webapp/namedentity/namedentity.js',
	function($){

		/**
		 * @class Webapp.devicegroups
		 */
		Webapp.NamedEntity('Webapp.eventsfunctionalchannel',
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
				serverController: jsRoutes.controllers.EventsFunctionalChannel,
				className: 'EventsFunctionalChannel',
				formTitle: 'functional channel'
			}
		},
		/** @Prototype */
		{
			init : function() {
				this._super();
			}
		});
});