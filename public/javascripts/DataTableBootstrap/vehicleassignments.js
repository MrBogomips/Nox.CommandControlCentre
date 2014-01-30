//Init***************************************************************************************************************
var oTable;
var vehicleList= [];
var driverList = [];
var wordlistAdd = [];
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
			                 		"sWidth": "1%",
			                 	},
			                 	{	"aTargets": [2],
			                 		"sTitle": "Driver",
			                 		"mData": "driverId",
			                 		"mRender": function ( data, type, val ) {
			                 			return fnReturnList( data, type, val, "driverId", driverList );
			                 		},
			                 		"sWidth": "1%",
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
			                 			return fnReturnActions( data, type, val, ["Save","Delete"], "vehicleassignments" );
			                 		},
									"bSearchable": false,
									"bSortable": false,
									"sWidth": "1%",
								},	
			               ],
			"fnDrawCallback": function( oSettings ) {
				//funzioni chiamate ad ogni redraw della tabella
				//selezione per colonne enable, vehicle e driver
		    	$('td:nth-last-child(2)').addClass("noClick");
		    	$('td:nth-child(2)').addClass("noClick");
		    	$('td:nth-child(3)').addClass("noClick");
		    	//selezione per colonnem data
				$('div.date input').click(function(e){
					e.stopPropagation();
					e.preventDefault();
				});
				fnReturnDrawCallback();
				fnActivateSwitch();
				fnManageDatapicker();
		    },
		    "fnInitComplete": function(){
		    	//funzioni chiamate quando la tabella è stata inizializzata
		    	fnReturnInitCallBack([0],wordlistAdd);	//autocompletamento colonne 0 più le colonne vehicle,driver e date (più la colonna enabled)
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
	$(".btn-save").click(function(el, ev) {
//		var options = {};
//		options["id"] = $(this).attr("data-vehicleassignments-id");
//		var $el = $("<div></div>");
//		$('body').append($el);
//		$el.webapp_vehicles(options);
		
		alert(el);
		if (el.hasClass('disabled')) return;
		var self = this;
		
		el.button('loading');
		self.container.block();
		
		jsRoutes.controllers.VehicleAssignement.update(self.options.id).ajax({
			data: self.element.find('select, input').serialize()
		})
		.done(function(data, txtStatus, jqXHR) {
			// SUCCESS
			el.button('reset');
			$version = self.element.find('input[name="version"]');
			$version.attr('value', 1 + parseInt($version.attr('value')));
			setTimeout(function() {
			    el.addClass('disabled');
			}, 0);
		})
		.fail(function() {
			// FAILURE
			el.button('reset');
		})
		.always(function(){
			self.container.unblock();
		});
	});

//	$(".btn-delete").click(function(el, ev) {
//		var id = $(this).attr("data-vehicleassignments-id");
//		jsRoutes.controllers.Vehicle.delete(id).ajax()
//		.done(function(data, txtStatus, jqXHR) {
//			location.reload(true);
//		})
//		.fail(function(data, txtStatus, jqXHR) {
//			var $alert= $("<div class='alert alert-block alert-error'><button type='button' class='close' data-dismiss='alert'>��</button><h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p></div>");
//			$(".alert_placeholder").html($alert);
//		});
//	});
}
//************************************************************************************************************************

//Global Functions********************************************************************************************************
//gestione global functions
function fnGlobalFunctions(){
//	$('.btn.create').click(function(el, ev) {
//		if (el.hasClass('disabled')) return;
//		var self = this;
//		$tr=$("<tr></tr>")
//		self.element.find('table').append($tr);
//		$tr.webapp_vehicle_assignements_row(self.options);
//		el.addClass('disabled')
//	});
//	$('.btn.save-all').click(function(el, ev) {
//		this.element.find('button.update[disabled!=disabled], button.save').click();
//	});
}

//aggiunta global actions
function fnAddGlobalFunctions(){
	$(".globalfunctions").html('<button class="btn btn-primary create">Create new vehicle assignment</button> \
								<button class="btn btn-primary save-all">Save all</button>');
}
//************************************************************************************************************************

//funzioni di supporto per la definizione della tabella vehicleassignment
function fnReturnDataPicker( data, type, val, name ) {
	wordlistAdd.push(data);
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
	var dispName = $.grep(vector, function(e){ return e.id == data; })[0].displayName;	//ritorna il displayName "selezionato"
	wordlistAdd.push(dispName);
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
        return dispName;
    }
}

//Gestione switch checkbox
function fnActivateSwitch(){
	$(".switch ").off('click').click(fnManageSwitch);
}

function fnManageSwitch(){
	$(this).bootstrapSwitch('toggleState');
}

//Gestione datapicker
function fnManageDatapicker(){
	var nowTemp = new Date();
	var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);
	var beginDatePicker = $(".date.beginAssignement");
	var endDatePicker = $(".date.endAssignement");
	var beginDate, endDate;
	beginDate = beginDatePicker.datepicker({
		onRender: function(date) {
		    return date.valueOf() < now.valueOf() ? 'disabled' : '';
		  }
	})
	.on('changeDate', function(ev){
		endDate = endDatePicker.data('datepicker');
		if (ev.date.valueOf() >= endDate.date.valueOf()) {
		    var newDate = new Date(ev.date)
		    newDate.setDate(newDate.getDate() + 1);
		    endDate.setValue(newDate);
		  } else {
			  endDate.setValue(endDate.date);  // force endDate redraw!!!
		  }
		beginDatePicker.datepicker('hide');
	})
	.data('datepicker');
	endDate = endDatePicker.datepicker({
		onRender: function(date) {
			return date.valueOf() <= beginDate.date.valueOf() ? 'disabled' : '';
		}
	}).on('changeDate', function(ev){
		endDatePicker.datepicker('hide');
	});
}