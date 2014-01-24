//Init***************************************************************************************************************
var oTable;
/* Table initialisation */
$(document).ready(function() {
	oTable = $('#vehicles').dataTable( {
		"aoColumnDefs": [
		                 	{	"aTargets": [0],
		                 		"sTitle": "Vehicle ID",
		                 		"mData": "id"
		                 	},
		                 	{	"aTargets": [1],
		                 		"sTitle": "name",
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
		                 		"sTitle": "Model",
		                 		"mData": "model"
		                 	},
		                 	{	"aTargets": [5],
		                 		"sTitle": "License Plate",
		                 		"mData": "licensePlate"
		                 	},
		                 	{	"aTargets": [6],
		                 		"sTitle": "enabled",
		                 		"mData": function ( data, type, val ) {
		                 			return fnReturnCheckbox( data, type, val );
		                 		},
		                 		"sWidth": "1%",
		                 	},
							{	"aTargets": [7],
		                 		"sTitle": "",
		                 		"mData": function ( data, type, val ) {
		                 			return fnReturnActionEditDelete( data, type, val );
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
			//filter autocomplete (colonne 1-5, e aggiunge gli stati della checkbox se necessario)
			if(window.location.search != "" && getQueryParam("all") == "false" ){
				fnAutoComplete([1,2,3,4,5]);
			}else{
				fnAutoComplete([1,2,3,4,5],["enabled","disabled"]);
			}
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
		options["id"] = $(this).attr("data-vehicle-id");
		var $el = $("<div></div>");
		$('body').append($el);
		$el.webapp_vehicles(options);
	});

	$(".btn-delete").click(function(el, ev) {
		var id = $(this).attr("data-vehicle-id");
		jsRoutes.controllers.Vehicle.delete(id).ajax()
		.done(function(data, txtStatus, jqXHR) {
			location.reload(true);
		})
		.fail(function(data, txtStatus, jqXHR) {
			var $alert= $("<div class='alert alert-block alert-error'><button type='button' class='close' data-dismiss='alert'>��</button><h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p></div>");
			$(".alert_placeholder").html($alert);
		});
	});
}
//************************************************************************************************************************

//Global Functions********************************************************************************************************
//gestione global functions
function fnGlobalFunctions(){
	$("#create_vehicle").click(function() {
		var $el = $("<div></div>")
		$('body').append($el);
		$el.webapp_vehicles();
	});
}

//aggiunta global actions
function fnAddGlobalFunctions(){
	$(".globalfunctions").html('<button class="btn btn-primary" id="create_vehicle">Create new vehicle</button>');
}
//************************************************************************************************************************