steal( '/assets/webapp/models/channels.js',
	   '/assets/webapp/models/devices.js')
.then( '/assets/webapp/table/views/table.ejs', 
	   './css/table.less',
	   '/assets/webapp/table/row/row.js',
	function($){

		/**
		 * @class Webapp.table
		 */
		Aria.Controller('Webapp.table',
		/** @Static */
		{
			defaults : {
				id : '',
				model : '',
				labels : [],
				values : []
			}
		},
		/** @Prototype */
		{
			init : function() {
				var that = this;
				this._super();
				this.element.addClass('webapp_table');
				this.numberRow = 0;
				this.WS_Channel = Aria.Page.getInstance().getChannelByName("WS_MQTT");
				this.WS_Channel.subscribe('new_data', that._updateContent);
				this._callView();
			} ,
			
			_updateContent : function(data) {
				$("#counter").html(parseInt($("#counter").html()) + 1);
				//console.log(data);
			},

			_callView : function() {
				var that = this;
				this.element.html('/assets/webapp/table/views/table.ejs', 
						{ 
							'id' : that.options.id , 
							'labels' : that.options.labels , 
							'values' : that.options.values 
						} );
			} ,

			_addRows : function(values) {
				var that = this;
				for (var i = 0; i < values.length; i++) {
					that._addRow(values[i]);	
				}
			} ,

			_addRow : function(values) {
					var that = this;
					that.numberRow = that.numberRow + 1;
					var idTr = 'row' + that.options.id + that.numberRow;
					$('#tbl' + that.options.id).children('tbody').append('<tr id="' + idTr +  '"></tr>');
					var newTr = this.element.find('#' + idTr);
					Webapp.row.newInstance(newTr, { 'id' : idTr , 'values' : values } );
				} ,

			_deleteRows : function() {
				var arrRows = this.element.find('tbody tr');
				for (var i = 0; i < arrRows.length; i++) {
					if ($(arrRows[i]).hasClass('noresults') == false) {
						$(arrRows[i]).remove();
					}
				}
			}


		});

});