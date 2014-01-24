//Init***************************************************************************************************************
var oTable;
/* Table initialisation */
$(document).ready(function() {
	oTable = $('#device').dataTable( {
        "sAjaxSource": '/device/index',
        "sAjaxDataProp": "",
		"aoColumnDefs": [
		                 	{	"aTargets": [0],
		                 		"sTitle": "ID",
		                 		"mData": "id"
		                 	},
		                 	{	"aTargets": [1],
		                 		"sTitle": "Device ID",
		                 		"mData": "name"
		                 	},
		                 	{	"aTargets": [2],
		                 		"sTitle": "Display Name",
		                 		"mData": "displayName"
		                 	},
		                 	{	"aTargets": [3],
		                 		"sTitle": "Description",
		                 		"mData": "description"
		                 	},
		                 	{	"aTargets": [4],
		                 		"sTitle": "Simcard",
		                 		"mData": "simcardDisplayName"
		                 	},
		                 	{	"aTargets": [5],
		                 		"sTitle": "Vehicle Installed",
		                 		"mData": "vehicleDisplayName"
		                 	},
		                 	{	"aTargets": [6],
		                 		"sTitle": "enabled",
		                 		"mData": function ( data, type, val ) {
		                 			if (type === 'set') {
		                 		        // Memorizza il valore base
		                 		        data.enabled = val;
		                 		 
		                 		        // Mostra una checkbox switch
		                 		        data.enabled_display = '<div class="switch"><input type="checkbox" ';
		                 		        if(val) data.enabled_display += 'checked';
		                 		        data.enabled_display += ' disabled ></div>';
		                 		 
		                 		        // Filtra in base allo stato (enabled/disabled)
		                 		        data.enabled_filter = val ? "enabled" : "disabled";
		                 		        return;
		                 		    }
		                 		    else if (type === 'display') {
		                 		        return data.enabled_display;
		                 		    }
		                 		    else if (type === 'filter') {
		                 		        return data.enabled_filter;
		                 		    }
		                 		    // 'sort', 'type' and undefined all just use the integer
		                 		    return data.enabled;
		                 		},
		                 		"sWidth": "1%",
		                 	},
							{	"aTargets": [7],
		                 		"sTitle": "",
		                 		"mData": function ( data, type, val ) {
		                 			return '<nobr>' +			
											    '<div class="btn-group">' +
												    '<a class="btn btn-danger dropdown-toggle" data-toggle="dropdown" data-target="#">' +
												    	'Actions' +
												    	'<span class="caret"></span>' +
												    '</a>' +
												    '<ul class="dropdown-menu pull-right">' +
												    	'<li><a tabindex="-1" data-target="#" class="btn-edit" data-device-id="@d.id">Edit</a></li>' +
												    	'<li><a tabindex="-1" data-target="#" class="btn-delete" data-device-id="@d.id">Delete</a></li>' +
												    '</ul>' +
											    '</div>' +
										    '</nobr>';
		                 		},
								"bSearchable": false,
								"bSortable": false,
								"sWidth": "1%",
							},	
		               ],
		"fnDrawCallback": function( oSettings ) {
			//funzioni chiamate ad ogni redraw della tabella
				// Gestione selezione righe
				fnActivateSelection();
		    	//attiva switch
		    	$('.switch:not(.has-switch)').bootstrapSwitch();
				// local actions
		    	fnLocalAction();
				// (necessarie per bootstrap-select
				$('.selectpicker').selectpicker();
	    },
	    "fnInitComplete": function(){
	    	//predispone colonna local actions
	    	$('tr:last-child').addClass("noRowSelected");
			$('td:last-child').addClass("noClick");
			//filter autocomplete
			fnAutoComplete();
	    },
	} );
	//init the table*****************************
	
	init();
	
	//aggiunta global functions
    fnAddGlobalFunctions();
    
	//Event bindings*****************************
	fnGlobalFunctions();
} );
//Init***************************************************************************************************************

//Local actions***********************************************************************************************************
//gestione local actions
function fnLocalAction(){
	$(".btn-edit").click(function(el, ev) {
		var options = {};
		options["id"] = $(this).attr("data-device-id");
		var $el = $("<div></div>");
		$('body').append($el);
		$el.webapp_device(options);
	});
		
	$(".btn-delete").click(function(el, ev) {
		var id = $(this).attr("data-device-id");
		jsRoutes.controllers.Device.delete(id).ajax()
		.done(function(data, txtStatus, jqXHR) {
			location.reload(true);
		})
		.fail(function(data, txtStatus, jqXHR) {
			var $alert= $("<div class='alert alert-block alert-error'><button type='button' class='close' data-dismiss='alert'>��</button><h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p></div>");
			self.find(".alert_placeholder").html($alert);
		});
	});
}
//************************************************************************************************************************

//Global Functions********************************************************************************************************
//gestione global functions
function fnGlobalFunctions(){
	$("#create_device").click(function() {            	
		var $el = $("<div></div>")
		$('body').append($el);
		$el.webapp_device();
	});
}

//aggiunta global actions
function fnAddGlobalFunctions(){
	$(".globalfunctions").html('<button class="btn btn-primary" id="create_device">Create new device</button>');
}
//************************************************************************************************************************

//************************************************************************************************************************
//imposta e attiva l'autocompletamento sul campo di ricerca
function fnAutoComplete(){
	var wordlist = []; 
	var table = oTable.$('tr');
	var colonna = [];
	for(var j=1; j<6; j++){	//per le colonne 2-6 //i<table[0].cells.length-1 per prendere tutta la riga meno la colonna action
		colonna[j]=[];
		for(var i=0; i<table.length; i++){
			colonna[j].push(table[i].cells.item(j).innerHTML);
		}
		colonna[j] = unique( colonna[j] );	//elimina duplicati
		$.merge(wordlist,colonna[j]);		//aggiunge la colonna alla wordlist
	}
	wordlist.push("enabled","disabled");	//aggiunge i due termini per la colonna delle checkbox
	wordlist = unique(wordlist); //elimina eventuali ducplicati (presenti nel caso in cui ci siano termini uguali in colonne diverse)
	wordlist.sort();	
	//attiva autocompletamento con la wordlist costruita
	$('.tablefilter input').autocomplete({source: wordlist});
}
//************************************************************************************************************************