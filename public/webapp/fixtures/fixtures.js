// map fixtures for this application

steal("jquery/dom/fixture", 
	function() {
		var bool = [ false , true ];

		$.fixture("/devices.json", function(orig, settings, headers) {
			var devices = new Array();
			var names = [ 'Andrea' , 'Fulvio' , 'Giovanni' , 'Wesley' , 'Samuele' , 'Marco' , 'Stefano' , 'Giuseppe' ];
			var classes = [ 'info' , 'default' , 'warning' , 'important' ];
			var messages = [ 'Position' , 'Info' , 'Warning' , 'Critical' ];
			var numberOfDevices = Math.floor(Math.random() * 10);
			for (x = 0; x < numberOfDevices; x++) {
				var events = new Array();
				for (i = 0; i < messages.length; i++) {
					events[i] = { 'code' : Math.floor(Math.random() * 1000) , 'class' : classes[i] , 'message' : messages[i] }
				}
				devices[x] = [ { 'type' : 'string' , 'description' : Math.floor(Math.random() * 100) , 'editable' : false } , 
							   { 'type' : 'alarm' , 'description' : bool[Math.floor(Math.random() * 2)] , 'editable' : false } , 
							   { 'type' : 'device' , 'description' : names[ Math.floor(Math.random() * names.length) ] , 'editable' : false } , 
							   { 'type' : 'events' , 'description' : events , 'editable' : false } ,
							   { 'type' : 'bool' , 'description' : bool[Math.floor(Math.random() * 2)] , 'editable' : false } ,
							   { 'type' : 'bool' , 'description' : bool[Math.floor(Math.random() * 2)] , 'editable' : false } ,
							   { 'type' : 'string' , 'description' : (Math.floor(Math.random() * 20000) / 100) + ' Km/h' , 'editable' : false } ,
							   { 'type' : 'button' , 'description' : 'more' , 'editable' : true } ];
			}
			return [ devices ];
		});

		$.fixture("/info.json", function(orig, settings, headers) {
			var info = { 'title' : orig.data.device ,
						 'general' : 'Lorem ipsum dolor sit amet, consectetur adipisici elit.' , 
						 'values' : [ [ { 'type' : 'string' , 'description' : 'Output 1' , 'editable' : false } , { 'type' : 'checkbox' , 'description' : bool[Math.floor(Math.random() * 2)] , 'editable' : bool[Math.floor(Math.random() * 2)] } ] ,
					  				  [ { 'type' : 'string' , 'description' : 'Output 2' , 'editable' : false } , { 'type' : 'checkbox' , 'description' : bool[Math.floor(Math.random() * 2)] , 'editable' : bool[Math.floor(Math.random() * 2)] } ] ,
						 			  [ { 'type' : 'string' , 'description' : 'Max Speed' , 'editable' : false } , { 'type' : 'string' , 'description' : Math.floor(Math.random() * 20000) / 100 , 'editable' : bool[Math.floor(Math.random() * 2)] } ] ,
						 			  [ { 'type' : 'string' , 'description' : 'Attribute N' , 'editable' : false } , { 'type' : 'string' , 'description' : Math.floor(Math.random() * 10000) , 'editable' : bool[Math.floor(Math.random() * 2)] } ] ]
						} ;
			return info;
		});

})