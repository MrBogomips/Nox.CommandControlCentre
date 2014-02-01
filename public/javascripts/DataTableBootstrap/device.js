//Init***************************************************************************************************************
var oTable;
/* Table initialisation */
$(document).ready(function() {
	oTable = $('#device').dataTable( {
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
		                 		"sTitle": "Enabled",
		                 		"mData": "enabled",
		                 		"mRender": function ( data, type, val ) {
		                 			return fnReturnCheckbox( data, type, val, false );
		                 		},
		                 		"sWidth": "1%",
		                 	},
							{	"aTargets": [7],
		                 		"sTitle": "",
		                 		"mData": "id",	//passa l'id alle action
		                 		"mRender": function ( data, type, val ) {
		                 			return fnReturnActions( data, type, val, ["Edit","Delete"], "device" );
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
       	    	fnReturnInitCallBack([1,2,3,4,5]);	//autocompletamento colonne 1-5 (più la colonna enabled)
       	    },
       	} );
       	//init the table*****************************
       	init();
} );
//Init***************************************************************************************************************

////Local actions***********************************************************************************************************
////gestione local actions
//function fnLocalAction(){
//	$(".btn-edit").click(function(el, ev) {
//		var options = {};
//		options["idname"] = "data-device-id";
//		options["id"] = $(this).attr("data-device-id");
//		var $el = $("<div></div>");
//		$('body').append($el);
//		$el.webapp_device(options);
//	});
//		
////	$(".btn-delete").click(function(el, ev) {
////		var id = $(this).attr("data-device-id");
////		jsRoutes.controllers.Device.delete(id).ajax()
////		.done(function(data, txtStatus, jqXHR) {
//////			location.reload(true);
////			oTable.fnReloadAjax();
////		})
////		.fail(function(data, txtStatus, jqXHR) {
////			//var $alert= $("<div class='alert alert-block alert-error'><button type='button' class='close' data-dismiss='alert'>��</button><h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p></div>");
////			//self.find(".alert_placeholder").html($alert);
////			popAlertError("<h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p>");
////		});
////	});
//	
//	$(".btn-delete").click(function(el, ev) {
//		var id = $(this).attr("data-device-id");
//		jsRoutes.controllers.Device.delete(id).ajax()
//		.done(function(data, txtStatus, jqXHR) {
////			popAlertSuccess("<h4 class='alert-heading'>Success</h4><p>"+data.responseText+"</p>");
//			oTable.fnDeleteRow( oTable.fnGetPosition( oTable.$('tr:has(td:has([data-device-id='+id+']))')[0] ) );
//			popAlertSuccess("<strong>Row "+id+" deleted successfully</strong>");
//		})
//		.fail(function(data, txtStatus, jqXHR) {
//			popAlertError("<h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p>");
//		});	
//	});
//}
////************************************************************************************************************************
//
////Global Functions********************************************************************************************************
////gestione global functions
//function fnGlobalFunctions(){
//	$("#create_device").click(function() {            	
//		var $el = $("<div></div>")
//		$('body').append($el);
//		$el.webapp_device();
//	});
//}
//
////aggiunta global actions
//function fnAddGlobalFunctions(){
//	$(".globalfunctions").html('<button class="btn btn-primary" id="create_device">Create new device</button>');
//}
////************************************************************************************************************************