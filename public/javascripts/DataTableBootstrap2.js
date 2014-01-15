/* Set the defaults for DataTables initialisation */
$.extend( true, $.fn.dataTable.defaults, {
	"sDom": "<'row'<'span6'<'tablefilter'>i><'span6'<'selectionbuttons'>>r>t<'row'<'span6'<'globalfunctions'>'><'span6'pl>>>",
	"sPaginationType": "bootstrap",
	"oLanguage": {
		'sLengthMenu': "Show " +
						"<select class='selectpicker' data-width='auto'>" +
								"<option value='10'>10 elements </option>" +
								"<option value='20'>20 elements </option>" +
								"<option value='50'>50 elements </option>" +
								"<option value='100'>100 elements </option>" +
						"</select>",
		"fnInfoCallback": function (oSettings, iStart, iEnd, iMax, iTotal, sPre) {
						        if(iTotal == iMax){
						            return "Found " + iTotal + " records";
						        }else if(iTotal == 0){
						        	return "No records found";
						        }else{
						            return "Found " + iTotal + " of " + iMax + " records";
						        }
						    },
		"sInfoFiltered": "",
		"oPaginate": {
			"sNext": "»",
			"sPrevious": "«"
	      }
	},
} );

/* Default class modification */
$.extend( $.fn.dataTableExt.oStdClasses, {
	"sWrapper": "dataTables_wrapper form-inline"
} );

/* API method to get paging information */
$.fn.dataTableExt.oApi.fnPagingInfo = function ( oSettings )
{
	return {
		"iStart":         oSettings._iDisplayStart,
		"iEnd":           oSettings.fnDisplayEnd(),
		"iLength":        oSettings._iDisplayLength,
		"iTotal":         oSettings.fnRecordsTotal(),
		"iFilteredTotal": oSettings.fnRecordsDisplay(),
		"iPage":          oSettings._iDisplayLength === -1 ?
			0 : Math.ceil( oSettings._iDisplayStart / oSettings._iDisplayLength ),
		"iTotalPages":    oSettings._iDisplayLength === -1 ?
			0 : Math.ceil( oSettings.fnRecordsDisplay() / oSettings._iDisplayLength )
	};
};

/* Bootstrap style pagination control */
$.extend( $.fn.dataTableExt.oPagination, {
	"bootstrap": {
		"fnInit": function( oSettings, nPaging, fnDraw ) {
			var oLang = oSettings.oLanguage.oPaginate;
			var fnClickHandler = function ( e ) {
				e.preventDefault();
				if ( oSettings.oApi._fnPageChange(oSettings, e.data.action) ) {
					fnDraw( oSettings );
				}
			};

			$(nPaging).addClass('pagination').append(
				'<ul>'+
					'<li class="prev disabled"><a href="#">'+oLang.sPrevious+'</a></li>'+
					'<li class="next disabled"><a href="#">'+oLang.sNext+'</a></li>'+
				'</ul>'
			);
			var els = $('a', nPaging);
			$(els[0]).bind( 'click.DT', { action: "previous" }, fnClickHandler );
			$(els[1]).bind( 'click.DT', { action: "next" }, fnClickHandler );
		},

		"fnUpdate": function ( oSettings, fnDraw ) {
			var iListLength = 5;
			var oPaging = oSettings.oInstance.fnPagingInfo();
			var an = oSettings.aanFeatures.p;
			var i, ien, j, sClass, iStart, iEnd, iHalf=Math.floor(iListLength/2);

			if ( oPaging.iTotalPages < iListLength) {
				iStart = 1;
				iEnd = oPaging.iTotalPages;
			}
			else if ( oPaging.iPage <= iHalf ) {
				iStart = 1;
				iEnd = iListLength;
			} else if ( oPaging.iPage >= (oPaging.iTotalPages-iHalf) ) {
				iStart = oPaging.iTotalPages - iListLength + 1;
				iEnd = oPaging.iTotalPages;
			} else {
				iStart = oPaging.iPage - iHalf + 1;
				iEnd = iStart + iListLength - 1;
			}

			for ( i=0, ien=an.length ; i<ien ; i++ ) {
				// Remove the middle elements
				$('li:gt(0)', an[i]).filter(':not(:last)').remove();

				// Add the new list items and their event handlers
				for ( j=iStart ; j<=iEnd ; j++ ) {
					sClass = (j==oPaging.iPage+1) ? 'class="active"' : '';
					$('<li '+sClass+'><a href="#">'+j+'</a></li>')
						.insertBefore( $('li:last', an[i])[0] )
						.bind('click', function (e) {
							e.preventDefault();
							oSettings._iDisplayStart = (parseInt($('a', this).text(),10)-1) * oPaging.iLength;
							fnDraw( oSettings );
						} );
				}

				// Add / remove disabled classes from the static elements
				if ( oPaging.iPage === 0 ) {
					$('li:first', an[i]).addClass('disabled');
				} else {
					$('li:first', an[i]).removeClass('disabled');
				}

				if ( oPaging.iPage === oPaging.iTotalPages-1 || oPaging.iTotalPages === 0 ) {
					$('li:last', an[i]).addClass('disabled');
				} else {
					$('li:last', an[i]).removeClass('disabled');
				}
			}
		}
	}
} );

//Init***************************************************************************************************************
var oTable;
var giRedraw = false;
/* Table initialisation */
$(document).ready(function() {
	//predispone colonna local actions
	$('thead tr').append('<th class="noRowSelected"></th>');
	$('tbody tr').append('<td class="noClick"></td>');
	fnShowHide(5);		
	
    //init the table*****************************
	oTable = $('#example').dataTable( {
		"bDestroy": true,		
		"aoColumnDefs": [
		                 	{ "sName": "RenderingEngine", "aTargets": [ 0 ] },
		                 	{ "sName": "Browser", "aTargets": [ 1 ] },
		                 	{ "sName": "Platform", "aTargets": [ 2 ] },
		                 	{ "sName": "EngineVersion", "aTargets": [ 3 ] },
		                 	{ "sName": "CSSgrade", "aTargets": [ 4 ] },
							{
		                 		"sName": "ActionColumn",
		                 		"sTitle": "",
								"aTargets": [5],
								"mData": null,
								"bSearchable": false,
								"bSortable": false,
								"mRender": function (data, type, full) {
									//aggiunta pulsanti ultima colonna in base al contenuto della riga
									if(full[1].search('Internet')!=-1){
										return fnAddLocalActions();
									}else if(full[1].search('Firefox')!=-1){
										return fnAddLocalActions1();
									}else{
										return fnAddLocalActions2();
									}
								},
							},
		               ],
		"fnDrawCallback": function( oSettings ) {
			//funzioni chiamate ad ogni redraw della tabella
				// Gestione selezione righe
				fnActivateSelection();
				// (necessarie per bootstrap-select
				$('.localfunctions .selectpicker').change(fnLocalAction);
				$('.selectpicker').selectpicker();
	    },
		"bAutoWidth": false,
		"bDeferRender": false,
	} );
	//init the table*****************************
	
	//Aggiunte***********************************
	//aggiunta global functions
    fnAddGlobalFunctions();
    //aggiunta selection buttons
    fnAddSelectionButtons();
    //aggiunta clear filter button
    fnAddTableFilter();
    //aggiunta selected info
	fnAddSelectedInfo();
	//Aggiunte***********************************
    
	//Event bindings*****************************
	//refresh button
	$(".btn:contains('Refresh')").click( function( e ) {		
		//TODO
	});
	//clear filter button
	$(".tablefilter .clear").click(function( e ) {
		$(".tablefilter input").val('');
		oTable.fnFilter('');
		fnUpdateNumSelected();
	});
	//search filter button
	$(".tablefilter .search").click(function( e ) {
		$('.tablefilter input').autocomplete("close");
		oTable.fnFilter($(".tablefilter input").val());
		fnUpdateNumSelected();
	});
	//global functions
	$('.globalfunctions .selectpicker').change(fnGlobalFunctions);
	//local actions
	$('.localfunctions .selectpicker').change(fnLocalAction);
	//selection buttons
	$('.selectionbuttons .selPage').click(function(){ fnSelectPage(); });
	$('.selectionbuttons .selAll').click(function(){ fnSelectAll(); });
	$('.selectionbuttons .deselPage').click(function(){ fnDeselectPage(); });
	$('.selectionbuttons .deselAll').click(function(){ fnDeselectAll(); });
	$('.selectionbuttons .showSel').click(function(){ fnShowSelected(); });
	//aggiorna i contatori righe selezionate dopo il filtering
	$('#example').bind('filter', function () {fnUpdateNumSelected(); });
	//Miglioria gui su firefox
	$('th').attr('unselectable', 'on').css('user-select', 'none').on('selectstart', false);
	//filter autocomplete
	var wordlist = []; 
	var data = oTable._('tr');
	var colonna = [];
	for(var i=0; i<3; i++){	//per le prime 3 colonne		
		colonna[i] = unique( data.map(function(value,index){return value[i];}) );	//estrae i dati unici dalla colonna
		$.merge(wordlist,colonna[i]);	//li aggiunge alla wordlist
	}
	$.each(colonna[1], function( index, value ) { colonna[1][index] = value.replace(/\d|\+|\-|\.|\r|\n|\f|\t/g, '').trim(); });	//estrae i nomi dei browser (senza versione)
	$.merge(wordlist,unique(colonna[1]));
	wordlist.sort();	
//	for(var i=0; i<3; i++){ $.merge(wordlist,unique( data.map(function(value,index){return value[i];}) ));}	//estrae i dati unici dalle colonne e li aggiunge alla wordlist
	$('.tablefilter input').autocomplete({source: wordlist});
//	$('.tablefilter input').autocomplete({
//			select: function( event, ui ) {
//				$('.tablefilter .search').click();
//			}
//		});
	//Event bindings*****************************
	
	//Enable Bootstrap-Select
	$('.selectpicker').selectpicker();
} );
//Init***************************************************************************************************************

//mostra/nasconde la collonna iCol
function fnShowHide( iCol )
{
	/* Get the DataTables object again - this is not a recreation, just a get of the object */
	var oTable = $('#example').dataTable();
	
	var bVis = oTable.fnSettings().aoColumns[iCol].bVisible;
	oTable.fnSetColumnVis( iCol, bVis ? false : true );
}

//elimina duplicati da array
function unique(array) {
    return $.grep(array, function(el, index) {
        return index == $.inArray(el, array);
    });
}

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

//Row selection***********************************************************************************************************
//aggiunta selection buttons
function fnAddSelectionButtons(){
	$(".selectionbuttons").html("<span> \
									<button type='button' class='btn btn-primary showSel' data-toggle='button'> \
										<i class='icon-eye-open icon-white'></i> \
										<div class='btn-label'> \
											Show Selected \
										</div> \
										<div class='selectedinfo'></div> \
									</button> \
								</span> \
								<div class='btn-group'> \
									<button type='button' class='btn selPage'> \
										<i class='icon-plus'></i> \
										Page \
									</button> \
									<button type='button' class='btn selAll'> \
										<i class='icon-plus'></i> \
										All \
									</button> \
									<button type='button' class='btn deselPage'> \
										<i class='icon-minus'></i> \
										Page \
									</button> \
									<button type='button' class='btn deselAll'> \
										<i class='icon-remove'></i> \
										Clear \
									</button> \
								</div>"
	);
}

//gestione select all
function fnSelectPage(){
	oTable.$('tr', {"page": "current"}).addClass('row_selected');
	fnUpdateNumSelected();
}
function fnSelectAll(){
	oTable.$('tr').addClass('row_selected');
	fnUpdateNumSelected();
}

//gestione select none
function fnDeselectPage(){
    oTable.$('tr.row_selected', {"page": "current"}).removeClass('row_selected');
    fnUpdateNumSelected();
    oTable.fnDraw();	//ridisegna la tabella per visualizzare solo le righe selezionate (se abilitato)
}
function fnDeselectAll(){
    oTable.$('tr.row_selected').removeClass('row_selected');
    fnUpdateNumSelected();
    oTable.fnDraw();	//ridisegna la tabella per visualizzare solo le righe selezionate (se abilitato)
}

//Gestione selezione righe
function fnManageSelection(){
	var cella = $(this);
	var riga = cella.parent();
	if(!cella.hasClass('noClick')){
		if ( riga.hasClass('row_selected') ) {
			riga.removeClass('row_selected');
			oTable.fnDraw();	//ridisegna la tabella per visualizzare solo le righe selezionate (se abilitato)
		}else {
			//oTable.$('tr.row_selected').removeClass('row_selected');	//decommentare per settare il single-row selection
			riga.addClass('row_selected');
		}
	}
	fnUpdateNumSelected();
}

//aggiorna counter
function fnUpdateNumSelected(){
	var div = $(".selectedinfo");
	var totSelected = fnGetSelected(oTable).length;
	div.children(".total").text(totSelected);
	div.children(".number").text( oTable.$('tr.row_selected', {"filter": "applied"}).length );
	if(totSelected > 0){
		div.show();
	}else{
		div.hide();
	}
}

//bind click
function fnActivateSelection(){
	$("td").off('click').click(fnManageSelection);
}

/* Get the rows which are currently selected */
function fnGetSelected( oTableLocal )
{
    return oTableLocal.$('tr.row_selected');
}
//************************************************************************************************************************

//aggiunta clear filter button
function fnAddTableFilter(){
	$(".tablefilter").html("<form onsubmit='return false;'> \
								<div class='input-append'> \
								    <input class='span2' type='text' placeholder='Search'> \
								    <button class='btn search' type='submit'><i class='icon-search'></i></button> \
								    <button class='btn clear' type='button'><i class='icon-remove'></i></button> \
							    </div> \
							</form>"	
	);
}

//aggiunta selected info
function fnAddSelectedInfo(){
	$(".selectedinfo").html(" (<span class='total'>0</span>)").hide();
}

//************************************************************************************************************************
/* Custom filtering function which will filter selected rows */
var ShowSelected = false;
function fnShowSelected(){
	if(ShowSelected = !ShowSelected){
		$('.selectionbuttons .showSel .btn-label').text("Hide Selected");
	}else{
		$('.selectionbuttons .showSel .btn-label').text("Show Selected");
	}
	oTable.fnDraw();
}

$.fn.dataTableExt.afnFiltering.push(
	function( oSettings, aData, iDataIndex ) {
		if(ShowSelected){
			//se la riga è selezionata la visualizza
			if($(oTable.fnGetNodes(iDataIndex)).hasClass('row_selected')){
				return true;
			}
			return false;
		}
		return true;
	}
);
//************************************************************************************************************************