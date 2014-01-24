//Init***************************************************************************************************************
var oTable;
/* Table initialisation */
$(document).ready(function() {
	oTable = $('#devicegroups').dataTable( {
		"aoColumnDefs": [
		                 	{	"aTargets": [0],
		                 		"sTitle": "ID",
		                 		"mData": "id"
		                 	},
		                 	{	"aTargets": [1],
		                 		"sTitle": "Name",
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
		                 		"sTitle": "enabled",
		                 		"mData": function ( data, type, val ) {
		                 			return fnReturnCheckbox( data, type, val );
		                 		},
		                 		"sWidth": "1%",
		                 	},
							{	"aTargets": [5],
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
//function fnLocalAction() definita nella view scala
//************************************************************************************************************************

//Global Functions********************************************************************************************************
//gestione global functions
//function fnGlobalFunctions() definita nella view scala

//aggiunta global actions
//function fnAddGlobalFunctions() definita nella view scala
//************************************************************************************************************************