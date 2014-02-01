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
				
				var jsonData = self.element.find('form').serialize();
				
				serverController.create().ajax({
					data: jsonData
				})
				.done(function(data, txtStatus, jqXHR) {
//					var jsonData = {};
//					var myForm = self.element.find('form');
//					//mette i dati del form in un json object 
//					myForm.serializeArray().map(function(x){
//						jsonData[x.name] = x.value;
//					});
//					//aggiunge l'id dell'oggetto creato
//					jsonData.id = data.id;
//					//aggiunge i displayName delle option selezionate
//					alert(myForm.find('.btn.selectpicker'));
//					myForm.find('.btn.selectpicker').each(function( selectedOption ) {
//						alert(selectedOption.attr('data-id'));
//						alert(selectedOption.children().first().text());
//						
////						jsonData[ selectElement.name.substring(0,selectElement.name.length - 2) + "DisplayName"] = selectElement.options[selectElement.selectedIndex].text;
//					});
//					alert( JSON.stringify(jsonData) );
//					oTable.fnAddData( jsonData );
//					popAlertSuccess("<strong>Record created successfully</strong>");
					/*location = serverController.index().url;*/
					oTable.fnReloadAjax();
				})
				.fail(function(data, txtStatus, jqXHR) {
					self.proxy(self._reportError(data, txtStatus, jqXHR));
				})
				.always(function(){
					self.element.unblock();
					self.element.modal('hide');
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
					oTable.fnReloadAjax();
				})
				.fail(function(data, txtStatus, jqXHR) {
					self.proxy(self._reportError(data, txtStatus, jqXHR));
				})
				.always(function(){
					self.element.unblock();
					self.element.modal('hide');
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