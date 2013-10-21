steal('/assets/webapp/namedentity/namedentity.js',
	function($){

		/**
		 * @class Webapp.devicegroups
		 */
		Webapp.NamedEntity('Webapp.MqttLogisticChannel',
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
				serverController: jsRoutes.controllers.MqttLogisticChannel,
				className: 'MqttLogisticChannel',
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