//Init***************************************************************************************************************
var oTable;
/* Table initialisation */
$(document).ready(function() {
	oTable = $('#maintenanceservices').dataTable( {
		"aoColumnDefs": [
							{	"aTargets": [0],
								"sTitle": "Id",
								"mData": "id"
							},
							{	"aTargets": [1],
								"sTitle": "Vehicle name",
								"mData": "vehicleName"
							},
							{	"aTargets": [2],
								"sTitle": "Service name",
								"mData": "serviceName"
							},
							{	"aTargets": [3],
								"sTitle": "Odometer (Km)",
								"mData": "serviceName"
							},
							{	"aTargets": [4],
								"sTitle": "Period (Mths)",
								"mData": "serviceDisplayName"
							},
							{	"aTargets": [5],
								"sTitle": "Enabled",
		                 		"mData": "enabled",
		                 		"mRender": function ( data, type, val ) {
		                 			return fnReturnCheckbox( data, type, val, false );
		                 		},
		                 		"sWidth": "1%",
							},
							{	"aTargets": [6],
								"sTitle": "",
								"mData": "id",	//passa l'id alle action
		                 		"mRender": function ( data, type, val ) {
									return fnReturnActions( data, type, val, ["Edit","Delete"], "maintenance-service" );
								},
								"bSearchable": false,
								"bSortable": false,
								"sWidth": "1%",
							},	
		               ],
	               "fnDrawCallback": function( oSettings ) {
		       			//funzioni chiamate ad ogni redraw della tabella
		       			fnReturnDrawCallback();
		       	    },
		       	    "fnInitComplete": function(){
		       	    	//funzioni chiamate quando la tabella è stata inizializzata
		       	    	fnReturnInitCallBack([0,1,2,3,4]);	//autocompletamento colonne 0-4 (più la colonna enabled)
		       	    },
		       	} );
		       	//init the table*****************************
		       	
		       	init();
   } );
//Init***************************************************************************************************************

//Local actions***********************************************************************************************************
//gestione local actions
function fnLocalAction(){
	$(".btn-edit").click(function(el, ev) {
		var options = {};
		options["id"] = $(this).attr("data-maintenance-service-id");
		var $el = $("<div></div>");
		$('body').append($el);
		$el.webapp_maintenanceservices(options);
	});
		
	$(".btn-delete").click(function(el, ev) {
		var id = $(this).attr("data-maintenance-service-id");
		jsRoutes.controllers.MaintenanceService.delete(id).ajax()
		.done(function(data, txtStatus, jqXHR) {
			location.reload(true);
		})
		.fail(function(data, txtStatus, jqXHR) {
			var $alert= $("<div class='alert alert-block alert-error'><button type='button' class='close' data-dismiss='alert'>x</button><h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p></div>");
			self.find(".alert_placeholder").html($alert);
		});
	});
}
//************************************************************************************************************************

//Global Functions********************************************************************************************************
//gestione global functions
function fnGlobalFunctions(){
	$("#create_maintenance_service").click(function() {            	
		var $el = $("<div></div>")
		$('body').append($el);
		$el.webapp_maintenanceservices();
	});
}

//aggiunta global actions
function fnAddGlobalFunctions(){
	$(".globalfunctions").html('<button class="btn btn-primary" id="create_maintenance_service">Create new service</button>');
}
//************************************************************************************************************************