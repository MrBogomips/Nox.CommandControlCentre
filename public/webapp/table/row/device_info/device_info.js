steal(
	function($){

		/**
		 * @class Webapp.row
		 */
		Aria.Controller('Webapp.device_info',
		/** @Static */
		{
			defaults : {
			}
		},
		
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('device_info');
					$.when(
						self.options.parent._getDeviceInfo(self.options)
					).then(
						function() {
							if (self.options.info == undefined) {
								self.options.info = null;
							}
							self.element.html('/assets/webapp/table/row/device_info/views/view.ejs', self.options, function(el) {
								var el = self.element.find('.modal.info');
								$el = $(el);
								$el.modal();
								$el.on('hidden', function(){
									self.element.html('');
									self.destroy();
								});
							});
						
							self.device = self.options.device;
							
							// ascolta sul canale degli eventi direttamente
							self.TrackingChannel = Aria.Page.getInstance().getChannelByName("tracking");
							
							self.TrackingChannel.subscribe('position info', self.proxy(self._updateInfo));
						}
					)
			},
			
			destroy : function(){
				var self = this;
				this.TrackingChannel.unsubscribe('info', this.proxy(self._updateInfo));
				this.TrackingChannel.unsubscribe('position', this.proxy(self._updateInfo));
			    this._super();
			},
			
			_updateInfo : function(event, data) {
				if (this.device == data.device) {
					var base = this.element.find(".extended-info table tbody");
					var props = data.data.objs;
					
					for(var k in props) {
						var e = base.find("span." + k);
						e.html(data.data.objs[k]);
					}
					
				}
			}
		});

});