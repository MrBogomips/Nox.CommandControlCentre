steal('jquery/model', 
	  'jquery/dom/fixture',
	function(){

		/**
		 * @class webapp.devices
		 * @parent index
		 * @inherits jQuery.Model
		 * Wraps backend bookslist services.  
		 */
		$.Model('webapp.models.devices',
		/* @Static */
		{
			findAll : function(params, success, error) {
					$.ajax({
						type: 'GET',
						url: '/devices.json',
						contentType: 'application/json; charset=utf-8',
						dataType: 'json',
						success: success ,
						error: error
					});
				} ,
		  	//findOne : "/channels/{id}.json", 
		  	//create : "/channels.json",
		 	//update : "/channels/{id}.json",
		  	//destroy : "/channels/{id}.json"
		},
		/* @Prototype */
		{});

})