steal(
	function($){

		/**
		 * @class Webapp.BaseForm
		 */
		Aria.Controller('Webapp.BaseForm',
		/** @Static */
		{
			defaults : {
				caller : null,
				uniqueId : 0
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('webapp_baseform');
			},

			destroy : function(){
				var self = this;
			    this._super();
			},
			
			_reportError : function(data, txtStatus, jqXHR) {
				var self = this;
				var errors = $.parseJSON(data.responseText);
				self._enumerateErrors(errors);
			},

			_enumerateErrors : function(errors) {
				var globalErrors = '';
				$.each(errors, function(label, value) {
					if ($('input[name="' + label + '"]').length > 0) {
						var obj = $('input[name="' + label + '"]');
						obj.closest('.control-group').addClass('error');
						var errorsList = '';
						for (var i = 0; i < value.length; i++) {
							errorsList += '<li style="font-size:12px;">' + value[i] + '</li>';
						}
						if (errorsList != '') {
							obj.closest('.controls').append('<span class="help-inline"><ul>' + errorsList + '</ul></span>');
						}
					}
					else {
						globalErrors += '<li style="font-size:12px;">' + value[i] + '</li>';
					}
				});
				if (globalErrors != '') {
					var $alert= $('<div class="alert alert-block alert-error"><button type="button" class="close" data-dismiss="alert">×</button><h4 class="alert-heading">An error occurred</h4><ul>' + globalErrors + '</ul></div>');
					this.find(".alert_placeholder").html($alert);
				}
			},

			_cancelErrors : function() {
				var self = this;
				self.find('.control-group').removeClass('error').find('.help-inline').remove();
			},

			'.btn.btn-cancel click' : function(el, ev) {
				this.element.modal('hide');
			},

			_create : function(serverController) {
				var self = this;
				self._cancelErrors();
				self.element.block();
				
				serverController.create().ajax({
					data: self.element.find('form').serialize()
				})
				.done(function(data, txtStatus, jqXHR) {
					location = serverController.index().url;
				})
				.fail(function(data, txtStatus, jqXHR) {
					self.proxy(self._reportError(data, txtStatus, jqXHR));
				})
				.always(function(){
					self.element.unblock();
				});
			},

			_update : function(serverController) {
				var self = this;
				self._cancelErrors();
				self.element.block();
				
				serverController.update(self.options.id).ajax({
					data: self.element.find('form').serialize()
				})
				.done(function(data, txtStatus, jqXHR) {
					location = serverController.index().url;
				})
				.fail(function(data, txtStatus, jqXHR) {
					self.proxy(self._reportError(data, txtStatus, jqXHR));
				})
				.always(function(){
					self.element.unblock();
				});
			},
			

			'.btn.device-create click' : function(el, ev) {
				var self = this;
				self._create(self.options.serverController);
			},

			'.btn.device-update click' : function(el, ev) {
				var self = this;
				self._update(self.options.serverController);
			}
		});

});