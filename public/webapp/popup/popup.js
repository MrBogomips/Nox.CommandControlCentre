steal( '/assets/webapp/popup/views/popup.ejs', 
	   './css/popup.less', 
	   '/assets/webapp/models/info.js',
	function($){

		/**
		 * @class Webapp.popup
		 */
		Aria.Controller('Webapp.popup',
		/** @Static */
		{
			defaults : {
				id : '',
				title : '',
				model : ''
			}
		},
		/** @Prototype */
		{
			init : function() {
				var that = this;
				this._super();
				this.element.addClass('webapp_popup');
				
				this.options.model.findAll(
					{ 'id' : that.options.id , 'device' : that.options.title } , 
					function(info) {
						that._create(info);
					} ,
					function() { 
						alert('error'); 
					}
				);
			} ,

			destroy : function() {
			    this.element.html('');
			    this._super();
			} ,

			_create : function(info) {
				var that = this;
				var html;
				var id = 'pop' + this.options.id;
				this.element.html('/assets/webapp/popup/views/popup.ejs', { 'id' : id , 'title' : info.title , 'general' : info.general , 'table' : { 'id' : that.options.id , 'labels' : [ 'Attribute' , 'Current Value' ] , 'values' : info.values } } );
			} ,

			'.restart.btn click' : function(el, ev) {
				alert('Restart');
			} ,

			'.shutdown.btn click' : function(el, ev) {
				alert('Shutdown');
			} ,

			'.repeat click' : function(el, ev) {
				alert('Repeat');
			} ,

			'.close click' : function(el, ev) {
				this.destroy();
			} ,

			'.cancel.btn click' : function(el, ev) {
				alert('Cancel');
			} ,

			'.save.btn click' : function(el, ev) {
				alert('Save');
			}


		});

});