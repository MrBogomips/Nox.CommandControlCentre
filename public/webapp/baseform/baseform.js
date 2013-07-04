steal(
	function($){

		/**
		 * @class Webapp.table
		 */
		Aria.Controller('Webapp.BaseForm',
		/** @Static */
		{
			defaults : {
				caller : null
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('webapp_baseform');
			} ,

			destroy : function(){
				var self = this;
			    this._super();
			},
			
			_reportError : function(data, txtStatus, jqXHR) {
				var $alert= $("<div class='alert alert-block alert-error'><button type='button' class='close' data-dismiss='alert'>×</button><h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p></div>");
				this.find(".alert_placeholder").html($alert);
			}
		});

});