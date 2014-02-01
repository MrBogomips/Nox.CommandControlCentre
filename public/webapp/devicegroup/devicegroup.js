steal('/assets/webapp/namedentity/namedentity.js',
	function($){

		/**
		 * @class Webapp.devicegroups
		 */
		Webapp.NamedEntity('Webapp.devicegroup',
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
				serverController: jsRoutes.controllers.DeviceGroup,
				className: 'devicegroup',
				formTitle: 'device group'
			}
		},
		/** @Prototype */
		{
			init : function() {
				this._super();
			}
		});

});