steal( '/assets/aria/steal/less/less',
	   '/assets/aria/aria/controller/controller',
	   '/assets/aria/jquery/view/ejs/ejs',
	   '/assets/webapp/models/channels.js',
	   '/assets/webapp/models/devices.js')
.then( '/assets/webapp/table/views/table.ejs', 
	   './css/table.less', 
	   '/assets/js/bootstrap.js', 
	   '/assets/css/bootstrap.css', 
	   '/assets/css/bootstrap-responsive.css',
	   '/assets/webapp/table/row/row.js',
	function($){

		/**
		 * @class Webapp.table
		 */
		Aria.Controller('Webapp.table',
		/** @Static */
		{
			defaults : {
				parent : '',
				id : '',
				content : ''
			}
		},
		/** @Prototype */
		{
			init : function() {
				var that = this;
				this.numberRow = 0;
				this._super();
				this.element.addClass('webapp_table');
				switch (this.options.id) {
					case 'ChannelSettings' :
						this.options.content.model.findAll(
							{  } , 
							function(d) {
								if (d.channels.length > 0) {
									that.options.numberRow = that.options.numberRow + d.channels.length;
									for (var i = 0; i < d.channels.length; i++) {
										that.options.content.values[i] = [ d.channels[i] , '' , '' ];
									}
									that._callView();
								}
							} ,
							function() { 
								alert('error'); 
							}
						);
						break;
					default :
						this._callView();
				}
				
			} ,

			_callView : function() {
				var that = this;
				this.element.html('/assets/webapp/table/views/table.ejs', { 'id' : that.options.id , 'content' : that.options.content } );
			} ,

			_addRows : function(values) {
				var that = this;
				for (var i = 0; i < values.length; i++) {
					that._addRow(that.options.id, that.options.content.types, values[i]);	
				}
			} ,

			_addRow : function(id, types, values){
					var that = this;
					that.numberRow = that.numberRow + 1;
					var idTr = 'row' + id + that.numberRow;
					$('#table' + id).find('tbody').append('<tr id="' + idTr +  '"></tr>');
					var newTr = this.element.find('#' + idTr);
					Webapp.row.newInstance(newTr, { 'id' : idTr , 'types' : types , 'values' : values } );
				} ,

			'.unsubscribe click' : function(el, ev) {
				var channel = $(el).closest('tr').attr('id');
			}


		});

});