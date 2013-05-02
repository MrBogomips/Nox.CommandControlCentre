steal( '/assets/aria/steal/less/less',
	   '/assets/aria/aria/controller/controller',
	   '/assets/aria/jquery/view/ejs/ejs')
.then( '/assets/webapp/table/row/views/row.ejs', 
	   './css/row.less', 
	   '/assets/js/bootstrap.js', 
	   '/assets/css/bootstrap.css', 
	   '/assets/css/bootstrap-responsive.css',
	   '/assets/webapp/checkbox/checkbox.js',
	   '/assets/webapp/popup/popup.js',
	function($){

		/**
		 * @class Webapp.row
		 */
		Aria.Controller('Webapp.row',
		/** @Static */
		{
			defaults : {
				id : '',
				types : '',
				values : ''
			}
		},
		/** @Prototype */
		{
			init : function(){
				var that = this;
				this._super();
				this.element.addClass('webapp_table_row');
				this.attributes = { 'id' : that.options.id , 'types' : that.options.types , 'values' : that.options.values };
				this.element.html('/assets/webapp/table/row/views/row.ejs', { 'id' : that.options.id , 'types' : that.options.types , 'values' : that.options.values } );
			} ,

			'.tool mousein' : function(el, ev) {
				$(el).tooltip('show');
			} ,

			'.tool mouseout' : function(el, ev) {
				$(el).tooltip('hide');
			} ,

			'.more .btn click' : function(el, ev) {
				var that = this;
				var anchor = $(el).closest('.more').find('.anchorInfo');
				$(anchor).webapp_popup({ 'id' : that.options.id , 'title' : that.options.values[2] , 'model' : webapp.models.info });
			}

		});

});