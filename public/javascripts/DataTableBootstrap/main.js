/* Set the defaults for DataTables initialisation */
$.extend( true, $.fn.dataTable.defaults, {
	"sDom": "<'riga'<'half'<'tablefilter'>i><'half'<'selectionbuttons'>r>><'clear'>t<'riga'<'half'<'globalfunctions'>><'half'pl>><'clear'>",
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
		"oPaginate": {
			"sNext": "»",
			"sPrevious": "«"
	      }
	},
	"bDestroy": true,
//	"bAutoWidth": false,
	"bDeferRender": false,
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

//ordinamento colonne checkbox
$.fn.dataTableExt.afnSortData['dom-checkbox'] = function ( oSettings, iColumn )
{
    var aData = [];
    $( 'td:eq('+iColumn+') input', oSettings.oApi._fnGetTrNodes(oSettings) ).each( function () {
        aData.push( this.checked==true ? "1" : "0" );
    } );
    alert(aData);
    return aData;
};


//Init***************************************************************************************************************
var oTable;

function init(){
	//Aggiunte***********************************
    //aggiunta selection buttons
    fnAddSelectionButtons();
    //aggiunta searchbox
    fnAddTableFilter();
    //aggiunta selected info
	fnAddSelectedInfo();
	//Aggiunte***********************************
    
	//Event bindings*****************************
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
	$('.selectpicker').selectpicker();
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