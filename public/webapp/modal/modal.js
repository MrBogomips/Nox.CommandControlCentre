steal( '/assets/webapp/modal/views/modal.ejs', 
	   './css/modal.less', 
	   '/assets/webapp/table/table.js',
		function($){

		/**
		 * @class Webapp.modal
		 */
		Aria.Controller('Webapp.modal',
		/** @Static */
		{
			defaults : {
				id : '',
				title : '',
				table : ''
			}
		},
		/** @Prototype */
		{
			init : function(){
				var that = this;
				this._super();
				this.element.addClass('webapp_modal');
				
				this.element.html('/assets/webapp/modal/views/modal.ejs', { 'id' : that.options.id , 'title' : that.options.title , 'table' : that.options.table } );

			}
		});

});