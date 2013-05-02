steal('jquery/model', 
	  'jquery/dom/fixture',
	function(){

		/**
		 * @class webapp.channels
		 * @parent index
		 * @inherits jQuery.Model
		 * Wraps backend bookslist services.  
		 */
		$.Model('webapp.models.channels',
		/* @Static */
		{
			findAll : function(params, success, error) {
					$.ajax({
						type: 'GET',
						url: '/channel/index',
						contentType: 'application/json; charset=utf-8',
						dataType: 'json channels.models',
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
