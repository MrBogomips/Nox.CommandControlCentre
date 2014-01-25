//Init***************************************************************************************************************
var oTable;
/* Table initialisation */
$(document).ready(function() {
	oTable = $('#drivers').dataTable( {
		"aoColumnDefs": [
		                 	{	"aTargets": [0],
		                 		"sTitle": "Driver ID",
		                 		"mData": "id"
		                 	},
		                 	{	"aTargets": [1],
		                 		"sTitle": "Name",
		                 		"mData": "name"
		                 	},
		                 	{	"aTargets": [2],
		                 		"sTitle": "Surname",
		                 		"mData": "surname"
		                 	},
		                 	{	"aTargets": [3],
		                 		"sTitle": "Display Name",
		                 		"mData": "displayName"
		                 	},
		                 	{	"aTargets": [4],
		                 		"sTitle": "Enabled",
		                 		"mData": "enabled",
		                 		"mRender": function ( data, type, val ) {
		                 			return fnReturnCheckbox( data, type, val, false );
		                 		},
		                 		"sWidth": "1%",
		                 	},
							{	"aTargets": [5],
		                 		"sTitle": "",
		                 		"mData": "id",	//passa l'id alle action
		                 		"mRender": function ( data, type, val ) {
		                 			return fnReturnActions( data, type, val, ["Edit","Delete"], "driver" );
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
		       	    	fnReturnInitCallBack([1,2,3]);	//autocompletamento colonne 1-3 (più la colonna enabled)
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
		options["id"] = $(this).attr("data-driver-id");
		var $el = $("<div></div>");
	$('body').append($el);
	$el.webapp_driver(options);
	});

	$(".btn-delete").click(function(el, ev) {
		var id = $(this).attr("data-driver-id");
		jsRoutes.controllers.Driver.delete(id)
			.ajax()
			.done(function(data, txtStatus, jqXHR) {
				location.reload(true);
			})
			.fail(function(data, txtStatus, jqXHR) {
				var $alert= $("<div class='alert alert-block alert-error'><button type='button' class='close' data-dismiss='alert'>×</button><h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p></div>");
				self.find(".alert_placeholder").html($alert);
			});
	});
}
//************************************************************************************************************************

//Global Functions********************************************************************************************************
//gestione global functions
function fnGlobalFunctions(){
	$("#create_driver").click(function() {
		var $el = $("<div></div>")
		$('body').append($el);
		$el.webapp_driver();
	});
}

//aggiunta global actions
function fnAddGlobalFunctions(){
	$(".globalfunctions").html('<button class="btn btn-primary" id="create_driver">Create new driver</button>');
}
//************************************************************************************************************************