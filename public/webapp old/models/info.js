steal('jquery/model', 
	  'jquery/dom/fixture',
	function(){

		/**
		 * @class webapp.info
		 * @parent index
		 * @inherits jQuery.Model
		 * Wraps backend bookslist services.  
		 */
		$.Model('webapp.models.info',
		/* @Static */
		{
			findAll : "/info.json",
		  	findOne : "/info/{id}.json", 
		  	//create : "/info.json",
		 	//update : "/info/{id}.json",
		  	//destroy : "/info/{id}.json"
		},
		/* @Prototype */
		{});

})