steal( '/assets/aria/steal/less/less',
	   '/assets/aria/aria/controller/controller',
	   '/assets/aria/jquery/view/ejs/ejs')
.then( '/assets/webapp/popup/views/popup.ejs', 
	   './css/popup.less', 
	   '/assets/js/bootstrap.js', 
	   '/assets/css/bootstrap.css', 
	   '/assets/css/bootstrap-responsive.css',
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
				$.when(html = $.View('popup/views/popup.ejs', { 'id' : info.id , 'title' : info.title , 'general' : info.general , 'attributes' : info.attributes , 'plug' : 'webapp_table' , 'idTable' : info.id , 'content' : { 'labels' : [ 'Attribute' , 'Current Value' ] , 'types' : [ 'string' , 'string' ] , 'values' : [] } }))
				.done(function(){
					that.element.append(html);
					for (var i = 0; i < info.attributes.length; i++) {
						$('#table' + info.id).closest('.webapp_table').controller()._addRow(info.id , [ 'string' , info.attributes[i].type ] , [ info.attributes[i].label , info.attributes[i].value ] );
					}
				});
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
			}


		});

});