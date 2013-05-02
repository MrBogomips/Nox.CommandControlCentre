steal( '/assets/aria/steal/less/less',
	   '/assets/aria/aria/controller/controller',
	   '/assets/aria/jquery/view/ejs/ejs')
.then( '/assets/Webapp/channels/items/views/items.ejs', 
	   './css/items.less', 
	function($){

		/**
		 * @class Webapp.items
		 */
		Aria.Controller('Webapp.items',
		/** @Static */
		{
			defaults : {
				parent : '',
				anchor : '',
				value : ''
			}
		},
		/** @Prototype */
		{
			init : function(){
				var that = this;
				this._super();
				this.element.addClass('webapp_items');
				$(that.options.anchor).html('/assets/Webapp/channels/items/views/items.ejs', { 'channel' : that.options.value });
			} ,

			'a mousedown' : function(el, ev) {
				if (($(el).parent('li').attr('id') != 'error') && ($(el).parent('li').attr('id') != 'noresults')) {
					this.options.parent._selectChannel($(el).parent('li').attr('id'));
				}
			} ,

		});

});