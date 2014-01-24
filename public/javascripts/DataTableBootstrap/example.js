//Init***************************************************************************************************************
var oTable;
/* Table initialisation */
$(document).ready(function() {
    //init the tables*****************************
	oTable = $('#example').dataTable( {
        "sAjaxSource": '/assets/json.txt',
		"aoColumnDefs": [
		                 	{	"aTargets": [0],
		                 		"sTitle": "Rendering engine",
		                 		"mData": "engine"
		                 	},
		                 	{	"aTargets": [1],
		                 		"sTitle": "Browser",
		                 		"mData": "browser"
		                 	},
		                 	{	"aTargets": [2],
		                 		"sTitle": "Platform(s)",
		                 		"mData": "platform"
		                 	},
		                 	{	"aTargets": [3],
		                 		"sTitle": "Engine version",
		                 		"mData": "version"
		                 	},
		                 	{	"aTargets": [4],
		                 		"sTitle": "CSS grade",
		                 		"mData": "grade"
		                 	},
							{	"aTargets": [5],
		                 		"sTitle": "",
		                 		"mData": function ( source, type, val ) {
										switch(source.action){
											case 'action0':
												return fnAddLocalActions();
											case 'action1':
												return fnAddLocalActions1();
											case 'action2':
												return fnAddLocalActions2();
										}
		                 	       },
								"bSearchable": false,
								"bSortable": false,
								"sWidth": "1%"
							}
		               ],
		"fnDrawCallback": function( oSettings ) {
			//funzioni chiamate ad ogni redraw della tabella
				// Gestione selezione righe
				fnActivateSelection();
				// (necessarie per bootstrap-select
				$('.localfunctions .selectpicker').change(fnLocalAction);
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
	//Aggiunte***********************************
    
	//Event bindings*****************************
	//refresh button
	$(".btn:contains('Refresh')").click( function( e ) {		
		//TODO
	});
	
	//global functions
	$('.globalfunctions .selectpicker').change(fnGlobalFunctions);
	//local actions
	$('.localfunctions .selectpicker').change(fnLocalAction);
	//Event bindings*****************************
} );
//Init***************************************************************************************************************

//Local actions***********************************************************************************************************
//gestione local actions
function fnLocalAction(){
	var select = $(this);
	var riga = select.parentsUntil('tr').parent();
    	switch(select.val()){
	    case 'l1':
	    	alert("Rendering engine: "+riga.children()[0].innerHTML); 
	      break;
	    case 'l2':
	    	alert("Browser: "+riga.children()[1].innerHTML);
	      break;
	    case 'l3':
	    	alert("Platform(s): "+riga.children()[2].innerHTML);
	      break;
	    case 'l4':
	    	alert("Engine version: "+riga.children()[3].innerHTML);
	      break;
	    case 'l5':
	    	alert("CSS Grade: "+riga.children()[4].innerHTML);
	      break;
	    case 'l6':
	    	alert("Rendering engine: "+riga.children()[0].innerHTML+"\r\n"+
	            	  "Browser: "+riga.children()[1].innerHTML+"\r\n"+
	            	  "Platform(s): "+riga.children()[2].innerHTML+"\r\n"+
	            	  "Engine version: "+riga.children()[3].innerHTML+"\r\n"+
	            	  "CSS Grade: "+riga.children()[4].innerHTML);
	    }
    $('.localfunctions .selectpicker').val('-1');
    $('.localfunctions .selectpicker').selectpicker('refresh');
}

//aggiunta local actions
function fnAddLocalActions(){
	return "<span class='localfunctions'> \
				<select class='selectpicker' data-style='btn-primary' data-width='auto'> \
					<option class='hide' value='-1'>Actions</option> \
					<option value='l1'>Print 1</option> \
					<option value='l2'>Print 2</option> \
					<option value='l3'>Print 3</option> \
					<option value='l4'>Print 4</option> \
					<option value='l5'>Print 5</option> \
					<option data-divider='true'></option> \
					<option value='l6'>Print All</option> \
				</select> \
			</span>";
}
function fnAddLocalActions1(){
	return "<span class='localfunctions'> \
				<select class='selectpicker' data-style='btn-danger' data-width='auto'> \
					<option class='hide' value='-1'>Actions</option> \
					<option value='l1'>Print 1</option> \
					<option value='l2'>Print 2</option> \
					<option value='l3'>Print 3</option> \
					<option value='l4'>Print 4</option> \
					<option value='l5'>Print 5</option> \
					<option data-divider='true'></option> \
					<option value='l6'>Print All</option> \
				</select> \
			</span>";
}
function fnAddLocalActions2(){
	return "<span class='localfunctions'> \
				<select class='selectpicker' data-style='btn-info' data-width='auto'> \
					<option class='hide' value='-1'>Actions</option> \
					<option value='l1'>Print 1</option> \
					<option value='l2'>Print 2</option> \
					<option value='l3'>Print 3</option> \
					<option value='l4'>Print 4</option> \
					<option value='l5'>Print 5</option> \
					<option data-divider='true'></option> \
					<option value='l6'>Print All</option> \
				</select> \
			</span>";
}
//************************************************************************************************************************

//Global Functions********************************************************************************************************
//gestione global functions
function fnGlobalFunctions(){
	var anSelected = fnGetSelected( oTable );
	var fun;
    if ( anSelected.length !== 0 ) {
    	switch($(this).val()){
	    case 'g1':
	    	fun=function(index) {
	    		alert("Rendering engine: "+anSelected[index].cells[0].innerHTML);
	    	}
	      break;
	    case 'g2':
	    	fun=function(index) {
	    		alert("Browser: "+anSelected[index].cells[1].innerHTML);
	    	}
	      break;
	    case 'g3':
	    	fun=function(index) {
	    		alert("Platform(s): "+anSelected[index].cells[2].innerHTML);
	    	}
	      break;
	    case 'g4':
	    	fun=function(index) {
	    		alert("Engine version: "+anSelected[index].cells[3].innerHTML);
	    	}
	      break;
	    case 'g5':
	    	fun=function(index) {
	    		alert("CSS Grade: "+anSelected[index].cells[4].innerHTML);
	    	}
	      break;
	    case 'g6':
	    	fun=function(index) {
    	    	alert("Rendering engine: "+anSelected[index].cells[0].innerHTML+"\r\n"+
	            	  "Browser: "+anSelected[index].cells[1].innerHTML+"\r\n"+
	            	  "Platform(s): "+anSelected[index].cells[2].innerHTML+"\r\n"+
	            	  "Engine version: "+anSelected[index].cells[3].innerHTML+"\r\n"+
	            	  "CSS Grade: "+anSelected[index].cells[4].innerHTML);
	    	}
	    }
    	$.each(anSelected,fun); 
    }else{
    	alert("No row selected");
    }
    $('.globalfunctions .selectpicker').val('-1'); 
}

//aggiunta global actions
function fnAddGlobalFunctions(){
	$(".globalfunctions").html("<span class='globaldrop rspace'> \
									<select class='selectpicker' data-style='btn-primary' data-header:'true' data-hide-disabled:'true' data-width='auto'> \
										<option class='hide' value='-1'>Pick an action</option> \
										<option value='g1'>Print 1</option> \
										<option value='g2'>Print 2</option> \
										<option value='g3'>Print 3</option> \
										<option value='g4'>Print 4</option> \
										<option value='g5'>Print 5</option> \
										<option data-divider='true'></option> \
										<option value='g6'>Print All</option> \
									</select> \
								</span> \
								<button type='button' class='btn'> \
										<i class='icon-refresh'></i> \
										Refresh \
								</button>"
	);
}
//************************************************************************************************************************

//************************************************************************************************************************
//imposta e attiva l'autocompletamento sul campo di ricerca
function fnAutoComplete(){
	var wordlist = []; 
	var table = oTable.$('tr');
	var colonna = [];
	for(var j=0; j<3; j++){	//per le prime 3 colonne //i<table[0].cells.length-1 per prendere tutta la riga meno la colonna action
		colonna[j]=[];
		for(var i=0; i<table.length; i++){
			colonna[j].push(table[i].cells.item(j).innerHTML);
		}
		colonna[j] = unique( colonna[j] );	//elimina duplicati
		$.merge(wordlist,colonna[j]);		//aggiunge la colonna alla wordlist
	}
	$.each(colonna[1], function( index, value ) { colonna[1][index] = value.replace(/\d|\+|\-|\.|\r|\n|\f|\t/g, '').trim(); });	//estrae i nomi dei browser (senza versione)
	$.merge(wordlist,unique(colonna[1]));	//li aggiunge alla wordlist (eliminando i duplicati)
	wordlist.sort();	
	//attiva autocompletamento con la wordlist costruita
	$('.tablefilter input').autocomplete({source: wordlist});
}
//************************************************************************************************************************