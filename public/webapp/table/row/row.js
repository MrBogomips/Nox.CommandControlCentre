steal( '/assets/webapp/table/row/device_info/device_info.js',
	   '/assets/webapp/table/row/device_settings/device_settings.js',
	function($){

		/**
		 * @class Webapp.row
		 */
		Aria.Controller('Webapp.row',
		/** @Static */
		{
			defaults : {
				id : '',
				values : ''
			}
		},
		/** @Prototype */
		{
			init : function() {
				var that = this;
				this._super();
				this.element.addClass('webapp_table_row');
				//this.attributes = { 'id' : that.options.id , 'values' : that.options.values };
				this.element.html('/assets/webapp/table/row/views/row2.ejs', that.options );
			} ,

			'.tool mousein' : function(el, ev) {
				$(el).tooltip('show');
			} ,

			'.tool mouseout' : function(el, ev) {
				$(el).tooltip('hide');
			} ,

			'.btn.settings click' : function(el, ev) {
				var ai = this.element.find('.anchorInfo'); 
				ai.webapp_device_settings(this.data);
			} ,
			
			'.btn.more click' : function(el, ev) {
				var ai = this.element.find('.anchorInfo'); 
				ai.webapp_device_info(this.data);
			} ,
			

			'#tblChannelSettings .btn.unsubscribe click' : function(el, ev) {
				var that = this;
				var channel = $.trim($(el).closest('tr').find('td.string').html());
				var channels = $('#channels').controller().channels;
				for (var i = 0; i < channels.length; i++) {
					if (channels[i].value == channel) {
						$('#channels').controller().channels[i].subscribed = false;
						$(el).closest('tr').remove();
					}
				}
			},
			
			updateData : function(data) {
				this.data = data;
				//console.log(data.data.ts);
				var e = $(this.element.find(".event-position-counter:eq(0)"));
				e.html(parseInt(e.html()) + 1);
			}

		});

});