steal('/assets/webapp/namedentity/namedentity.js',
	function($){

		/**
		 * @class Webapp.devicegroups
		 */
		Webapp.NamedEntity('Webapp.eventslogisticchannel',
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
				serverController: jsRoutes.controllers.EventsLogisticChannel,
				className: 'EventsLogisticChannel',
				formTitle: 'logistic channel'
			}
		},
		/** @Prototype */
		{
			init : function() {
				this._super();
			}
		});
});