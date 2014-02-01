steal(
	function($){

		/**
		 * @class Webapp.ModalForm
		 */
		Aria.Controller('Webapp.ModalForm',
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
				this.element.addClass('modal hide fade');
			},

			destroy : function(){
				var self = this;
			    this._super();
			},
			
			renderForm : function(view) {
				var self = this;
				self.element.html(view, self.options, function(el) {
					self.element.find(".selectpicker").selectpicker();
					self.element.find(".switch").bootstrapSwitch();
					if (self.options.validationErrors != undefined)
						self._enumerateErrors(self.options.validationErrors);
					self.element.modal('show');
					self.element.on('hidden', function(){
						self.element.html('');
						self.destroy();
					});
				});
			},
			
			_reportError : function(data, txtStatus, jqXHR) {
				var self = this;
				var errors = $.parseJSON(data.responseText);
				self._enumerateErrors(errors);
			},

			_enumerateErrors : function(errors) {
				if (errors == undefined) return;
				var globalErrors = '';
				var counter = 0;
				$.each(errors, function(label, value) {
					var obj = $('input[name="' + label + '"],select[name="' + label + '"]');
					if (obj.length > 0) {
						obj.closest('.control-group').addClass('error');
						var errorsList = '';
						for (var i = 0; i < value.length; i++) {
							errorsList += '<li class="error-inline"><span class="counter">' + ++counter + '</span> ' + value[i] + '</li>';
						}
						if (errorsList != '') {
							obj.closest('.controls').append('<div class="help-inline"><ul class="error-inline">' + errorsList + '</ul></div>');
						}
					}
					else {
						globalErrors += '<li style="font-size:12px;"><em>' + label + '</em>: ' + value + '</li>';
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
					var jsonData = {};
					self.element.find('select, input').serializeArray().map(function(x){
						jsonData[x.name] = x.value;
					});
					//aggiunge campi mancanti
					jsonData.id = data.id;
					jsonData.version = 0;
					jsonData.enabled = (jsonData.enabled == undefined) ? false : jsonData.enabled;	
					//aggiunge i displayName delle option selezionate
					var selectControl = self.element.find('.controls:has(.selectpicker)');
					var len = selectControl.length;
					for(var i=0; i<len; i++){
						var campo = $(selectControl[i]).find('.selectpicker').attr('name').slice(0,-2).concat("DisplayName");
						var valore = $(selectControl[i]).find('.selected .text').text().replace("[None]","");
						jsonData[campo] = valore;
					}
					oTable.fnAddData( jsonData );
					self.element.modal('hide');
					popAlertSuccess("<strong>Record created successfully.</strong>");
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
//					location = serverController.index().url;
//					oTable.fnReloadAjax();
					var jsonData = {};
					self.element.find('select, input').serializeArray().map(function(x){
						jsonData[x.name] = x.value;
					});
					//aggiunge campi mancanti
					jsonData.id = self.options.id;
//					jsonData.version = 0;
					jsonData.enabled = (jsonData.enabled == undefined) ? false : jsonData.enabled;	
					//aggiunge i displayName delle option selezionate
					var selectControl = self.element.find('.controls:has(.selectpicker)');
					var len = selectControl.length;
					for(var i=0; i<len; i++){
						var campo = $(selectControl[i]).find('.selectpicker').attr('name').slice(0,-2).concat("DisplayName");
						var valore = $(selectControl[i]).find('.selected .text').text().replace("[None]","");
						jsonData[campo] = valore;
					}
					oTable.fnUpdate( jsonData , oTable.fnGetPosition( oTable.$('tr:has(td:has(['+self.options.idname+'='+jsonData.id+']))')[0] ), undefined, false);
					oTable.fnStandingRedraw();
					self.element.modal('hide');
					popAlertSuccess("<strong>Record updated successfully.</strong>");
				})
				.fail(function(data, txtStatus, jqXHR) {
					self.proxy(self._reportError(data, txtStatus, jqXHR));
				})
				.always(function(){
					self.element.unblock();
				});
			},

			'.btn.create click' : function(el, ev) {
				var self = this;
				self._create(self.options.serverController);
			},

			'.btn.update click' : function(el, ev) {
				var self = this;
				self._update(self.options.serverController);
			}
		});

});