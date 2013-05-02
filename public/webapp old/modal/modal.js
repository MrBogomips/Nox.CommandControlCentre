steal( '/assets/aria/steal/less/less',
	   '/assets/aria/aria/controller/controller',
	   '/assets/aria/jquery/view/ejs/ejs')
.then( '/assets/webapp/modal/views/modal.ejs', 
	   './css/modal.less', 
	   '/assets/js/bootstrap.js', 
	   '/assets/css/bootstrap.css', 
	   '/assets/css/bootstrap-responsive.css',
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
				plug : '',
				content : ''
			}
		},
		/** @Prototype */
		{
			init : function(){
				var that = this;
				this._super();
				this.element.addClass('webapp_modal');
				
				this.element.html('/assets/webapp/modal/views/modal.ejs', { 'id' : that.options.id , 'title' : that.options.title , 'plug' : that.options.plug , 'content' : that.options.content } );

			}
		});

});