steal( '/assets/webapp/checkbox/views/checkbox.ejs', 
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
				value : true ,
				editable : true ,
				callback : null ,
				parametres : {}
			}
		},
		/** @Prototype */
		{
			init : function(){
				var that = this;
				this._super();
				this.element.addClass('webapp_checkbox');
				var value = this.options.value == true ? 'on' : 'off';
				$('#' + this.options.id).html('/assets/webapp/checkbox/views/checkbox.ejs', { 'id' : that.options.id , 'editable' : that.options.value.editable , 'value' : value });
			} ,

			'.switch click' : function(el, ev) {
				var that = this;
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
					if (this.options.callback != null) {
						this.options.callback(that.options.parametres);
					}
				}
			}

		});

});