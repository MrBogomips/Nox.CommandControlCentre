//Init***************************************************************************************************************
var oTable;
var vehicleList= [];
var driverList = [];
/* Table initialisation */
$(document).ready(function() {
	//preleva dati su veicoli e piloti****************************************
	var jsonReq = {
		headers : { 
			'Accept' : 'application/json; charset=utf-8',
			'Content-Type' : 'application/json; charset=utf-8'
		}	
	}
	var vehiclesIndex = jsRoutes.controllers.Vehicle.index();
	var driversIndex = jsRoutes.controllers.Driver.index();
	//Effettua richieste ajax
	$.when(vehiclesIndex.ajax(jsonReq), driversIndex.ajax(jsonReq))
	.done(function(vehs, drvs) {
		vehicleList = vehs[0];
		driverList = drvs[0];
	})
	.then(function(){
		//init the table*****************************
		oTable = $('#vehicleassignments').dataTable( {
			"aoColumnDefs": [
			                 	{	"aTargets": [0],
			                 		"sTitle": "ID",
			                 		"mData": "id"
			                 	},
			                 	{	"aTargets": [1],
			                 		"sTitle": "Vehicle",
			                 		"mData": "vehicleId",
			                 		"mRender": function ( data, type, val ) {
			                 			return fnReturnList( data, type, val, "vehicleId", vehicleList );
			                 		},
			                 	},
			                 	{	"aTargets": [2],
			                 		"sTitle": "Driver",
			                 		"mData": "driverId",
			                 		"mRender": function ( data, type, val ) {
			                 			return fnReturnList( data, type, val, "driverId", driverList );
			                 		},
			                 	},
			                 	{	"aTargets": [3],
			                 		"sTitle": "Begin Assignment",
			                 		"mData": "beginAssignement",
			                 		"mRender": function ( data, type, val ) {
			                 			return fnReturnDataPicker( data, type, val,"beginAssignement" );
			                 		},
			                 	},
			                 	{	"aTargets": [4],
			                 		"sTitle": "End Assignment",
			                 		"mData": "endAssignement",
		                 			"mRender": function ( data, type, val ) {
			                 			return fnReturnDataPicker( data, type, val,"endAssignement" );
			                 		},
			                 	},
			                 	{	"aTargets": [5],
			                 		"sTitle": "Enabled",
			                 		"mData": "enabled",
			                 		"mRender": function ( data, type, val ) {
			                 			return fnReturnCheckbox( data, type, val, true );
			                 		},
			                 		"sWidth": "1%",
			                 	},
								{	"aTargets": [6],
			                 		"sTitle": "",
			                 		"mData": "id",	//passa l'id alle action
			                 		"mRender": function ( data, type, val ) {
			                 			return fnReturnActions( data, type, val, ["Edit","Delete"], "vehicleassignments" );
			                 		},
									"bSearchable": false,
									"bSortable": false,
									"sWidth": "1%",
								},	
			               ],
			"fnDrawCallback": function( oSettings ) {
				//funzioni chiamate ad ogni redraw della tabella
				//predispone colonna enable
		    	$('tr:nth-last-child(2)').addClass("noRowSelected");
		    	$('td:nth-last-child(2)').addClass("noClick");
		    	$('td:nth-last-child(3)').addClass("noClick");	//temp
		    	$('td:nth-last-child(4)').addClass("noClick");	//temp
		    	$('td:nth-last-child(5)').addClass("noClick");	//temp
		    	$('td:nth-last-child(6)').addClass("noClick");	//temp
				fnReturnDrawCallback();
				fnActivateSwitch();
		    },
		    "fnInitComplete": function(){
		    	//funzioni chiamate quando la tabella è stata inizializzata
		    	fnReturnInitCallBack([0,1,2]);	//autocompletamento colonne 0-2 (più la colonna enabled)
		    },
		} );
		//init the table*****************************
		init();
	});
} );
//Init***************************************************************************************************************

//Local actions***********************************************************************************************************
//gestione local actions
function fnLocalAction(){
	$(".btn-edit").click(function(el, ev) {
		var options = {};
		options["id"] = $(this).attr("data-vehicleassignments-id");
		var $el = $("<div></div>");
		$('body').append($el);
		$el.webapp_vehicles(options);
	});

	$(".btn-delete").click(function(el, ev) {
		var id = $(this).attr("data-vehicleassignments-id");
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
	$('.btn.create').click(function(el, ev) {
		if (el.hasClass('disabled')) return;
		var self = this;
		$tr=$("<tr></tr>")
		self.element.find('table').append($tr);
		$tr.webapp_vehicle_assignements_row(self.options);
		el.addClass('disabled')
	});
	$('.btn.save-all').click(function(el, ev) {
		this.element.find('button.update[disabled!=disabled], button.save').click();
	});
}

//aggiunta global actions
function fnAddGlobalFunctions(){
	$(".globalfunctions").html('<button class="btn btn-primary create">Create new vehicle assignment</button> \
								<button class="btn btn-primary save-all">Save all</button>');
}
//************************************************************************************************************************

//funzioni di supporto per la definizione della tabella vehicleassignment
function fnReturnDataPicker( data, type, val, name ) {
	if (type === 'display') {
        return '<div class="input-append date '+name+'" data-date-format="yyyy-mm-dd" data-date="'+data+'"> \
		        	<input class="span2" type="text" readonly="" value="'+data+'" size="10" name="'+name+'"> \
			        <span class="add-on"> \
			        <i class="icon-th"></i> \
			        </span> \
		        </div>';
	}else{
		// 'sort', 'type' and filter
        return data;
    }
}

function fnReturnList( data, type, val, listId, vector ){
	if (type === 'display') {
		var id; var name;
		var content = '<select class="selectpicker span2" name="'+listId+'">';
		for(i in vector){
			id = vector[i].id;
			name = vector[i].displayName;
			content += '<option value="'+id+'"';
			if(data == id){
				content += ' selected';
			}
			content += '>'+name+'</option>';
		}
		content += '</select>';
		return content;
	}else{
		// 'sort', 'type' and filter
        return $.grep(vector, function(e){ return e.id == data; })[0].displayName;	//ritorna il displayName "selezionato"
    }
}

//Gestione switch checkbox
function fnActivateSwitch(){
	$(".switch ").off('click').click(fnManageSwitch);
}

function fnManageSwitch(){
	$(this).bootstrapSwitch('toggleState');
}