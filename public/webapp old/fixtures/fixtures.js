// map fixtures for this application

steal("jquery/dom/fixture", 
	function() {

		$.fixture("/devices.json", function(orig, settings, headers) {
			var devices = new Array();
			var names = [ 'Andrea' , 'Fulvio' , 'Giovanni' , 'Wesley' , 'Samuele' , 'Marco' , 'Stefano' , 'Giuseppe' ];
			var classes = [ 'info' , 'default' , 'warning' , 'important' ];
			var messages = [ 'Position' , 'Info' , 'Warning' , 'Critical' ];
			var bool = [ false , true ];
			var numberOfDevices = Math.floor(Math.random() * 10);
			for (x = 0; x < numberOfDevices; x++) {
				var events = new Array();
				for (i = 0; i < messages.length; i++) {
					events[i] = { 'code' : Math.floor(Math.random() * 1000) , 'class' : classes[i] , 'message' : messages[i] }
				}
				devices[x] = [ Math.floor(Math.random() * 100) , 
							   Math.floor(Math.random() * 2) , 
							   names[ Math.floor(Math.random() * names.length) ] , 
							   events ,
							   bool[Math.floor(Math.random() * 2)] ,
							   bool[Math.floor(Math.random() * 2)] ,
							   (Math.floor(Math.random() * 20000) / 100) + ' Km/h' ];
			}
			return [ devices ];
		});

		$.fixture("/info.json", function(orig, settings, headers) {
			var info =  { 'id' : 'pop' + orig.data.id , 
					  'title' : orig.data.device ,
					  'general' : 'GENERAL INFO' , 
					  'attributes' : [ { 'name' : 'OUT1' , 'label' : 'Output 1' , 'type' : 'checkbox' , 'editable' : true , 'value' : false } ,
						 			  { 'name' : 'OUT2' , 'label' : 'Output 2' , 'type' : 'checkbox' , 'editable' : false , 'value' : true } ,
						 			  { 'name' : 'MAX' , 'label' : 'Max Speed' , 'type' : 'string' , 'editable' : true , 'value' : (Math.floor(Math.random() * 20000) / 100) } ,
						 			  { 'name' : 'ATTR' , 'label' : 'Attribute N' , 'type' : 'string' , 'editable' : false , 'value' : '1234' }
						 			 ]
						   } ;
			return info;
		});

})