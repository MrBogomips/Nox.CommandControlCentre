//Init***************************************************************************************************************
//var oTable;
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
		oTable = $('#vehicleassignement').dataTable( {
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
			                 			return fnReturnActions( data, type, val, ["Save","Delete"], "vehicleassignement" );
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
					fnEnableDisableGlobalSave();
				});
		    },
		    "fnInitComplete": function(){
		    	//funzioni chiamate quando la tabella ?? stata inizializzata
		    	fnReturnInitCallBack([0],wordlistAdd);	//autocompletamento colonne 0 pi?? le colonne vehicle,driver e date (pi?? la colonna enabled)
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
	$(".btn-save").off('click').click(function(el, ev) {
		container.block();
		var self = $(this);
//		var nRow = self.closest('tr');
		var id = self.attr("data-vehicleassignement-id");
		var nRow = self.parents('tr');
//		var container = self.closest('table').parent();	
//		self.button('loading');
		if(self.attr("data-vehicleassignement-id") != '?'){
			jsRoutes.controllers.VehicleAssignement.update($(this).attr("data-vehicleassignement-id")).ajax({
				data: nRow.find('select, input').serialize()
			})
			.done(function(data, txtStatus, jqXHR) {
	//			// SUCCESS
	//			el.button('reset');
				$version = nRow.find('input[name="version"]');
				$version.attr('value', 1 + parseInt($version.attr('value')));
				fnDisableLocalSave(self);
				fnEnableDisableGlobalSave();
//				var jsonData = {};
//				nRow.find('select, input').serializeArray().map(function(x){
//					jsonData[x.name] = x.value;
//				});
////				alert(JSON.stringify(jsonData));
//				oTable.fnUpdate(jsonData, nRow[0] );
				popAlertSuccess("<strong>Data saved correctly.</strong>");
			})
			.fail(function() {
	//			// FAILURE
	//			el.button('reset');
//				popAlertError("<strong>An error occurred.</strong>");
				oTable.fnReloadAjax();
				popAlertError("<h4 class='alert-heading'>An error occurred</h4>");
			})
			.always(function(){
				container.unblock();
				$('.btn.create').removeClass('disabled');
			});
		}else{
			jsRoutes.controllers.VehicleAssignement.create().ajax({
				data: nRow.find('select, input').serialize()
			})
			.done(function(data, txtStatus, jqXHR) {
				fnDisableLocalSave(self);
				fnEnableDisableGlobalSave();
				//aggiorna l'id di riga con quello ritornato dal server
				var jsonData = {};
				nRow.find('select, input').serializeArray().map(function(x){
					jsonData[x.name] = x.value;
				});
				//aggiunge campi mancanti
				jsonData.id = data.id;
				jsonData.version = 0;
				jsonData.enabled = (jsonData.enabled == undefined) ? false : jsonData.enabled;
				oTable.fnUpdate( jsonData , nRow[0]);
				$('td:first', nRow).append('<input type="hidden" value="'+jsonData.id+'" name="id"> \
											<input type="hidden" value="'+jsonData.version+'" name="version">');
				popAlertSuccess("<strong>Record created successfully.</strong>");
			})
			.fail(function(data, txtStatus, jqXHR) {
				popAlertError("<h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p>");
			})
			.always(function(){
				container.unblock();
				$('.btn.create').removeClass('disabled');
			});
		}
	});

	$(".btn-delete").off('click').click(function(el, ev) {
		container.block();
		var self = $(this);
		var id = self.attr("data-vehicleassignement-id");
		if(id != '?'){
			jsRoutes.controllers.VehicleAssignement.delete(id).ajax()
			.done(function(data, txtStatus, jqXHR) {
				oTable.fnDeleteRow( oTable.fnGetPosition( oTable.$('tr:has(td:has([data-vehicleassignement-id='+id+']))')[0] ) );
				popAlertSuccess("<strong>Row deleted successfully.</strong>");
			})
			.fail(function(data, txtStatus, jqXHR) {
//				var $alert= $("<div class='alert alert-block alert-error'><button type='button' class='close' data-dismiss='alert'>??????</button><h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p></div>");
//				$(".alert_placeholder").html($alert);
				popAlertError("<h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p>");
			})
			.always(function(){
				container.unblock();
			});
		}else{
			$('.btn.create').removeClass('disabled');
			oTable.fnDeleteRow( self.closest('tr')[0] );
			container.unblock();
		}	
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
		fnEnableDisableGlobalSave();
	});
}

function clickEnableButton(enabledButtons, index){
	$(enabledButtons[index]).click()
};

//aggiunta global actions
function fnAddGlobalFunctions(){
	$(".globalfunctions").html('<button class="btn btn-primary create">Create new assignment</button> \
								<button class="btn btn-primary save-all disabled">Save all <span class="modifiedinfo"></span></button>');
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
	fnEnableDisableGlobalSave();
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
		fnEnableDisableGlobalSave();
	})
	.data('datepicker');
	endDate = endDatePicker.datepicker({
		onRender: function(date) {
			return date.valueOf() <= beginDate.date.valueOf() ? 'disabled' : '';
		}
	}).on('changeDate', function(ev){
		endDatePicker.datepicker('hide');
		fnEnableLocalSave(this);
		fnEnableDisableGlobalSave();
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

//gestione abilitazione global save
function fnEnableDisableGlobalSave(){
	var modifiedElementsNum = oTable.$('[data-modified=true]').length;
	var button = $('.btn.save-all');
	if( modifiedElementsNum == 0){
		button.find('.modifiedinfo').text("");
		button.addClass('disabled');
	}else{
		button.find('.modifiedinfo').text("("+modifiedElementsNum+")");
		button.removeClass('disabled');
	}
}