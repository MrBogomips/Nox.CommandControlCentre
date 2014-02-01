/* Set the defaults for DataTables initialisation */
$.extend( true, $.fn.dataTable.defaults, {
    "sAjaxSource": window.location.pathname+window.location.search,
    "sAjaxDataProp": "",
	"sDom": "<'riga'<'half'<'tablefilter'>i><'half'<'selectionbuttons'>r>><'clear'>t<'riga'<'half'<'globalfunctions'>><'half'pl>><'clear'>",
	"sPaginationType": "bootstrap",
	"oLanguage": {
		'sLengthMenu': "Show \
						<select class='selectpicker' data-width='auto' data-hide-disabled='true'> \
								<option value='10'>10 elements </option> \
								<option value='20'>20 elements </option> \
								<option value='50'>50 elements </option> \
								<option value='100'>100 elements </option> \
								<option value='-1'>All elements</option> \
						</select>",
		"fnInfoCallback": function (oSettings, iStart, iEnd, iMax, iTotal, sPre) {
						        if(iTotal == iMax){
						            return "Found " + iTotal + " records";
						        }else if(iTotal == 0){
						        	return "No records found";
						        }else{
						            return "Found " + iTotal + " of " + iMax + " records";
						        }
						    },
		"oPaginate": {
			"sNext": "»",
			"sPrevious": "«"
	      }
	},
	"bDestroy": true,
//	"bAutoWidth": false,
	"bDeferRender": true,
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

/*
 * TableTools Bootstrap compatibility
 * Required TableTools 2.1+
 */
if ( $.fn.DataTable.TableTools ) {
	// Set the classes that TableTools uses to something suitable for Bootstrap
	$.extend( true, $.fn.DataTable.TableTools.classes, {
		"container": "DTTT btn-group",
		"buttons": {
			"normal": "btn",
			"disabled": "disabled"
		},
		"collection": {
			"container": "DTTT_dropdown dropdown-menu",
			"buttons": {
				"normal": "",
				"disabled": "disabled"
			}
		},
		"print": {
			"info": "DTTT_print_info modal"
		},
		"select": {
			"row": "active"
		}
	} );

	// Have the collection use a bootstrap compatible dropdown
	$.extend( true, $.fn.DataTable.TableTools.DEFAULTS.oTags, {
		"collection": {
			"container": "ul",
			"button": "li",
			"liner": "a"
		}
	} );
}

//Redraw the table (i.e. fnDraw) to take account of sorting and filtering, but retain the current pagination settings.
$.fn.dataTableExt.oApi.fnStandingRedraw = function(oSettings) {
    if(oSettings.oFeatures.bServerSide === false){
        var before = oSettings._iDisplayStart;
 
        oSettings.oApi._fnReDraw(oSettings);
 
        // iDisplayStart has been reset to zero - so lets change it back
        oSettings._iDisplayStart = before;
        oSettings.oApi._fnCalculateEnd(oSettings);
    }
      
    // draw the 'current' page
    oSettings.oApi._fnDraw(oSettings);
};


//By default DataTables only uses the sAjaxSource variable at initialisation time, however it can be useful to re-read an Ajax source and have the table update. Typically you would need to use the fnClearTable() and fnAddData() functions, however this wraps it all up in a single function call.
$.fn.dataTableExt.oApi.fnReloadAjax = function ( oSettings, sNewSource, fnCallback, bStandingRedraw )
{
    // DataTables 1.10 compatibility - if 1.10 then versionCheck exists.
    // 1.10s API has ajax reloading built in, so we use those abilities
    // directly.
    if ( $.fn.dataTable.versionCheck ) {
        var api = new $.fn.dataTable.Api( oSettings );
 
        if ( sNewSource ) {
            api.ajax.url( sNewSource ).load( fnCallback, !bStandingRedraw );
        }
        else {
            api.ajax.reload( fnCallback, !bStandingRedraw );
        }
        return;
    }
 
    if ( sNewSource !== undefined && sNewSource !== null ) {
        oSettings.sAjaxSource = sNewSource;
    }
 
    // Server-side processing should just call fnDraw
    if ( oSettings.oFeatures.bServerSide ) {
        this.fnDraw();
        return;
    }
 
    this.oApi._fnProcessingDisplay( oSettings, true );
    var that = this;
    var iStart = oSettings._iDisplayStart;
    var aData = [];
 
    this.oApi._fnServerParams( oSettings, aData );
 
    oSettings.fnServerData.call( oSettings.oInstance, oSettings.sAjaxSource, aData, function(json) {
        /* Clear the old information from the table */
        that.oApi._fnClearTable( oSettings );
 
        /* Got the data - add it to the table */
        var aData =  (oSettings.sAjaxDataProp !== "") ?
            that.oApi._fnGetObjectDataFn( oSettings.sAjaxDataProp )( json ) : json;
 
        for ( var i=0 ; i<aData.length ; i++ )
        {
            that.oApi._fnAddData( oSettings, aData[i] );
        }
         
        oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
 
        that.fnDraw();
 
        if ( bStandingRedraw === true )
        {
            oSettings._iDisplayStart = iStart;
            that.oApi._fnCalculateEnd( oSettings );
            that.fnDraw( false );
        }
 
        that.oApi._fnProcessingDisplay( oSettings, false );
 
        /* Callback user function - for event handlers etc */
        if ( typeof fnCallback == 'function' && fnCallback !== null )
        {
            fnCallback( oSettings );
        }
    }, oSettings );
};

//Init***************************************************************************************************************
var oTable;
var container = $('.page-container');
container.block();

function init(){
	//Aggiunte***********************************
    //aggiunta selection buttons
    fnAddSelectionButtons();
    //aggiunta searchbox
    fnAddTableFilter();
    //aggiunta selected info
	fnAddSelectedInfo();
	//aggiunta global functions
    fnAddGlobalFunctions();		//definita per la singola tabella nel js specifico
	//Aggiunte***********************************
    
	//Event bindings*****************************
	fnGlobalFunctions();		//definita per la singola tabella nel js specifico
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
	//selection buttons
	$('.selectionbuttons .selPage').click(function(){ fnSelectPage(); });
	$('.selectionbuttons .selAll').click(function(){ fnSelectAll(); });
	$('.selectionbuttons .deselPage').click(function(){ fnDeselectPage(); });
	$('.selectionbuttons .deselAll').click(function(){ fnDeselectAll(); });
	$('.selectionbuttons .showSel').click(function(){ fnShowSelected(); });
	//Miglioria gui su firefox
	$('th').attr('unselectable', 'on').css('user-select', 'none').on('selectstart', false);
	//Event bindings*****************************
	
	//Enable Bootstrap-Select
//	$('.selectpicker').selectpicker();
}
//Init***************************************************************************************************************

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
			if(ShowSelected){	//ridisegna la tabella per eliminare le righe deselezionate in modalità visualizza selezionate
				oTable.fnDraw();
			}
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
//	div.children(".number").text( oTable.$('tr.row_selected', {"filter": "applied"}).length );
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

//aggiunta searchbox
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
		$('.selectionbuttons .selPage,.selAll').attr('disabled', true);
	}else{
		$('.selectionbuttons .showSel .btn-label').text("Show Selected");
		$('.selectionbuttons .selPage,.selAll').attr('disabled', false);
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

//elimina duplicati da array
function unique(array) {
    return $.grep(array, function(el, index){
        return index == $.inArray(el, array);
    });
}

//************************************************************************************************************************
//imposta e attiva l'autocompletamento sul campo di ricerca
function fnAutoComplete(columnlist, wordlistAppend){
	wordlistAppend = (typeof wordlistAppend === "undefined") ? [] : wordlistAppend;
	var wordlist = []; 
	var table = oTable.$('tr');
	var colonna = [];
	for(var j in columnlist){	//per le colonne 1-5 //i<table[0].cells.length-1 per prendere tutta la riga meno la colonna action
		if (Object.prototype.hasOwnProperty.call(columnlist, j)) {	//controllo per evitare di aggiungere altre proprietà della classe Array
			j=columnlist[j];	//prende l'indice effettivo e non l'index dell'indice all'interno di columnlist
			colonna[j]=[];
			for(var i=0; i<table.length; i++){
				colonna[j].push(table[i].cells.item(j).innerHTML);
			}
			colonna[j] = unique( colonna[j] );	//elimina duplicati
			$.merge(wordlist,colonna[j]);		//aggiunge la colonna alla wordlist
		}
	}
	wordlist.push.apply(wordlist,wordlistAppend);	//aggiunge i termini in wordlistAppend alla wordlist
	wordlist = unique(wordlist); //elimina eventuali ducplicati (presenti nel caso in cui ci siano termini uguali in colonne diverse)
	wordlist.sort();	
	//attiva autocompletamento con la wordlist costruita
	$('.tablefilter input').autocomplete({source: wordlist});
}
//************************************************************************************************************************

//prende parametro da querystring
function getQueryParam(param) {
    var result =  window.location.search.match(
        new RegExp("(\\?|&)" + param + "(\\[\\])?=([^&]*)")
    );

    return result ? result[3] : false;
}

//Imposta il menù di paginazione
function fnSetLengthMenu(){
	menu = $('.selectpicker');
	rowNumber = oTable.fnGetData().length;
	//elimina le opzioni "inutili"
	for(i in options=[10,20,50,100]){
		val=options[i];
		if(rowNumber < val) menu.find('option[value="'+val+'"]').attr('disabled', true).attr('selected', false);
	}
	$('.selectpicker').selectpicker('refresh');
}

//************************************************************************************************************************
//funzioni di supporto per la definizione di una tabella datatable
function fnReturnCheckbox( data, type, val, enabled ) {
	if (type === 'display') {
        // Mostra una checkbox switch
        var enabled_display = '<div class="switch"><input type="checkbox" name="enabled" ';
        if(data) enabled_display += 'checked';
        if(!enabled) enabled_display += ' disabled';
        enabled_display += '></div>';
        
        return enabled_display;
	}else if (type === 'filter') {
        return (data ? "enabled" : "disabled");
    }else{
    // 'sort', 'type'
    return data;
    }
}

function fnReturnActions( data, type, val, actions, controllername) {
	var content = '<div class="btn-group actions"> \
				   <a class="btn btn-danger dropdown-toggle" data-toggle="dropdown" data-target="#"> \
				   Actions \
				   <span class="caret"></span> \
				   </a> \
				   <ul class="dropdown-menu pull-right">';
	for(i in actions){
		content += '<li><a tabindex="-1" data-target="#" class="btn-'+actions[i].toLowerCase().replace(/ /g,"-")+'" data-'+controllername+'-id="'+data+'">'+actions[i]+'</a></li>';
	}
	content += '</ul> \
			    </div>';
	return content;
}

function fnReturnDrawCallback(){
	//predispone colonna local actions
	$('tr:last-child').addClass("noRowSelected");
	$('td:last-child').addClass("noClick");
	// Gestione selezione righe
	fnActivateSelection();
	//attiva switch
	$('.switch:not(.has-switch)').bootstrapSwitch();
	// local actions
	fnLocalAction();	//definita per la singola tabella nel js specifico
	// (necessarie per bootstrap-select
	$('.selectpicker').selectpicker();
}

function fnReturnInitCallBack(columnlist, vector ){
	vector = (typeof vector === "undefined") ? ["enabled","disabled"] : vector;
	//imposta il menù di paginazione
	fnSetLengthMenu();
	//filter autocomplete (colonne 1-5, e aggiunge gli stati della checkbox se necessario)
	if(window.location.search != "" && getQueryParam("all") == "false" ){
		fnAutoComplete(columnlist);
	}else{
		fnAutoComplete(columnlist,vector);
	}
	container.unblock();
}
//************************************************************************************************************************

function popAlertError(message){
	$("<div class='alert alert-danger fade in'><a class='close' data-dismiss='alert'>x</a>"+message+"</div>")
	.appendTo("#alert-box")
	.delay(1000).fadeTo(500, 0).slideUp(500, function(){$(this).alert('close');});
}

function popAlertSuccess(message){
//	$("<div class='alert alert-success fade in'><a class='close' data-dismiss='alert'>x</a>"+message+"</div>")
//	.appendTo("#alert-box")
//	.delay(1000).fadeTo(500, 0).slideUp(500, function(){$(this).alert('close');});
	$("<div class='alert alert-success fade in'><a class='close' data-dismiss='alert'>x</a>"+message+"</div>")
	.appendTo("#alert-box")
	.delay(1000).fadeTo(500, 0).slideUp(500, function(){
        $(this).alert('close');
	});
}