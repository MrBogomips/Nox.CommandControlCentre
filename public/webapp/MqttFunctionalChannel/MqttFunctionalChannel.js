steal('/assets/webapp/namedentity/namedentity.js',
	function($){

		/**
		 * @class Webapp.devicegroups
		 */
		Webapp.NamedEntity('Webapp.MqttFunctionalChannel',
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
				serverController: jsRoutes.controllers.MqttFunctionalChannel,
				className: 'MqttFunctionalChannel',
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