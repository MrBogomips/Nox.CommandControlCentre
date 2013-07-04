steal(
	function($){

		/**
		 * @class Webapp.BaseForm
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
				$('.control-group').removeClass('error').find('.help-inline').remove();
			},

			prova : function(self) {
				self._cancelErrors();
				jsRoutes.controllers.Vehicle.update(self.options.id).ajax({
					data: self.element.find('form').serialize(),
					success: function(data, txtStatus, jqXHR) {
						location = jsRoutes.controllers.Vehicle.index().url;
					},
					error: function(data, txtStatus, jqXHR) {
						self.proxy(self._reportError(data, txtStatus, jqXHR));
					}
				});
			}	
			
		});

});