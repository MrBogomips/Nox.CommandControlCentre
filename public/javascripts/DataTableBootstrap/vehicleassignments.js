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
			                 		"mData": "id",
			                 		"mRender": function ( data, type, val ) {
			                 			return fnReturnId( data, type, val );
			                 		},
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
		    	$('td:nth-child(6)').addClass("noClick");
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
				//disabilita update per le righe non modificate
				$("[data-modified=false]").find(".btn-save").parent().addClass('disabled');
				//riabilita l'update per le righe se cambia il valore delle select
				$('.selectpicker').on('change',function(){
					fnEnableLocalSave(this);
				});
		    },
		    "fnInitComplete": function(){
		    	//funzioni chiamate quando la tabella è stata inizializzata
		    	fnReturnInitCallBack([0],wordlistAdd);	//autocompletamento colonne 0 più le colonne vehicle,driver e date (più la colonna enabled)
		    	//disabilita update per le righe non modificate
		    	oTable.$("[data-modified=false]").find(".btn-save").parent().addClass('disabled');
		    },
		    "fnCreatedRow": function( nRow, aData, iDataIndex){
		    	$('td:last', nRow).attr('data-modified',false);
		    	$('td:first', nRow).append('<input type="hidden" value="'+aData.id+'" name="id"> \
		    								<input type="hidden" value="'+aData.version+'" name="version">');
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
		var self = $(this);
//		var nRow = self.closest('tr');
		var nRow = self.parents('tr');
		var container = self.closest('table').parent();	
//		self.button('loading');
		container.block();	
		if(self.attr("data-vehicleassignments-id") != '?'){
			jsRoutes.controllers.VehicleAssignement.update($(this).attr("data-vehicleassignments-id")).ajax({
				data: nRow.find('select, input').serialize()
			})
			.done(function(data, txtStatus, jqXHR) {
	//			// SUCCESS
	//			el.button('reset');
				popAlertSuccess("<strong>Data saved correctly.</strong>");
				$version = nRow.find('input[name="version"]');
				$version.attr('value', 1 + parseInt($version.attr('value')));
				fnDisableLocalSave(self);
			})
			.fail(function() {
	//			// FAILURE
	//			el.button('reset');
//				popAlertError("<strong>An error occurred.</strong>");
				popAlertError("<h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p>");
			})
			.always(function(){
				container.unblock();
				$('.btn.create').removeClass('disabled');
				oTable.fnReloadAjax();
//				oTable.fnStandingRedraw();
			});
		}else{
			jsRoutes.controllers.VehicleAssignement.create().ajax({
				data: nRow.find('select, input').serialize()
			})
			.done(function(data, txtStatus, jqXHR) {
				popAlertSuccess("<strong>Record created successfully.</strong>");
				self.attr("data-vehicleassignments-id",data.id);
				nRow.find('td:first').html(data.id);
				fnDisableLocalSave(self);
//				oTable.fnReloadAjax();
//				location.reload(true);
			})
			.fail(function(data, txtStatus, jqXHR) {
				popAlertError("<h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p>");
			})
			.always(function(){
				container.unblock();
				$('.btn.create').removeClass('disabled');
//				oTable.fnStandingRedraw();
				oTable.fnReloadAjax();
			});
		}
	});

	$(".btn-delete").click(function(el, ev) {
		var self = $(this);
//		var nRow = self.closest('tr').get(0);
		var nRow = self.parents('tr')[0];
		var id = self.attr("data-vehicleassignments-id");
		if(id != '?'){
			jsRoutes.controllers.VehicleAssignement.delete(id).ajax()
			.done(function(data, txtStatus, jqXHR) {
				oTable.fnReloadAjax();
//				oTable.fnDeleteRow( nRow  );
//				oTable.fnStandingRedraw();
//				oTable.fnDraw();
				popAlertSuccess("<strong>Row deleted successfully.</strong>");
//				location.reload(true);
			})
			.fail(function(data, txtStatus, jqXHR) {
//				var $alert= $("<div class='alert alert-block alert-error'><button type='button' class='close' data-dismiss='alert'>��</button><h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p></div>");
//				$(".alert_placeholder").html($alert);
				popAlertError("<h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p>");
			});
		}else{
			oTable.fnReloadAjax();
//			oTable.fnDeleteRow( nRow );
//			oTable.fnDraw();
//			oTable.fnStandingRedraw();
//			$('.btn.create').removeClass('disabled');
//			location.reload(true);
		}	
//		oTable.fnDraw();
//		oTable.fnReloadAjax();
	});
}
//************************************************************************************************************************

//Global Functions********************************************************************************************************
//gestione global functions
function fnGlobalFunctions(){
	$('.btn.create').click(function(el, ev) {
		var self = $(this);
		if (self.hasClass('disabled')) return;
		oTable.fnAddData([{"id":'?',"vehicleId":vehicleList[0].id,"driverId":driverList[0].id,"beginAssignement":"","endAssignement":"","enabled":true}]);
		self.addClass('disabled');
	});
	$('.btn.save-all').click(function(el, ev) {
		//trigger click event on changed rows (enabled save)
//		oTable.$(".btn-group.actions > ul > li:not(.disabled) > .btn-save").click();     
		var enabledButtons = oTable.$(".btn-group.actions > ul > li:not(.disabled) > .btn-save");
		var enabledButtonsCount = enabledButtons.length;
		var counter = 0;	
		while(counter < enabledButtonsCount){
			setTimeout(clickEnableButton, 1000 * counter, enabledButtons, counter);
			counter += 1;
		}
	});
}

function clickEnableButton(enabledButtons, index){
	$(enabledButtons[index]).click()
};

//aggiunta global actions
function fnAddGlobalFunctions(){
//	$(".globalfunctions").html('<button class="btn btn-primary create">Create new vehicle assignment</button> \
//								<button class="btn btn-primary save-all">Save all</button>');
	$(".globalfunctions").html('<button class="btn btn-primary create">Create new vehicle assignment</button>');
}
//************************************************************************************************************************

//funzioni di supporto per la definizione della tabella vehicleassignment
function fnReturnId( data, type, val ) {
	if (type === 'sort') {
		if(data == '?'){
			return -1;
		}else{
			return data;
		}
    }else{
    	//display, 'type' and filter
        return data;
    }
}

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
	var dispName;	
	var selected = $.grep(vector, function(e){ return e.id == data; })[0];	//individua l'elemento selezionato
	if(typeof selected != 'undefined'){
		dispName = selected.displayName;
		wordlistAdd.push(dispName);
	}	
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
	fnEnableLocalSave(this);
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
		$(".btn-save").parent().addClass('disabled');
		fnEnableLocalSave(this);
	})
	.data('datepicker');
	endDate = endDatePicker.datepicker({
		onRender: function(date) {
			return date.valueOf() <= beginDate.date.valueOf() ? 'disabled' : '';
		}
	}).on('changeDate', function(ev){
		endDatePicker.datepicker('hide');
		fnEnableLocalSave(this);
	});
}

//abilitazione local save
function fnEnableLocalSave(el){
	var row = $(el).closest('tr');
	row.find('td:last').attr('data-modified',true);
	row.find(".btn-save").parent().removeClass('disabled');
}
//abilitazione local save
function fnDisableLocalSave(el){
	var row = $(el).closest('tr');
	row.find('td:last').attr('data-modified',false);
	row.find(".btn-save").parent().addClass('disabled');
}