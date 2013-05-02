steal( '/assets/aria/steal/less/less',
	   '/assets/aria/aria/controller/controller',
	   '/assets/aria/jquery/view/ejs/ejs')
.then( '/assets/webapp/checkbox/views/checkbox.ejs', 
	   './css/checkbox.less',
	function($){

		/**
		 * @class Webapp.checkbox
		 */
		Aria.Controller('Webapp.checkbox',
		/** @Static */
		{
			defaults : {
				parent : '',
				id : '' ,
				editable : true ,
				value : 'on'
			}
		},
		/** @Prototype */
		{
			init : function(){
				var that = this;
				this._super();
				this.element.addClass('webapp_checkbox');
				$('#' + this.options.id).html('/assets/webapp/checkbox/views/checkbox.ejs', { 'id' : that.options.id , 'editable' : that.options.editable , 'value' : that.options.value });
			} ,

			'.switch click' : function(el, ev) {
				if (this.options.editable == true) {
					if ($(el).hasClass('on') == true) {
						$(el).removeClass('on');
						$(el).addClass('off');
						$(el).find('input').attr('checked' , false);
					}
					else {
						$(el).removeClass('off');
						$(el).addClass('on');
						$(el).find('input').attr('checked' , true);
					}
					this._recall();
				}
			} ,

			_recall : function() {
				
			}



		});

});