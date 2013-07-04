steal(
	function($){

		/**
		 * @class Webapp.table
		 */
		Aria.Controller('Webapp.device',
		/** @Static */
		{
			defaults : {
				id : '',
				model : '',
				name : '',
				display_name : '',
				description: '',
				type_id: '',
				group_id: '',
				creation_time: '',
				enabled: true,
				modification_time: '',
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('webapp_device');
				
				var renderForm = function() {
							self.element.html(jsRoutes.controllers.Assets.at("webapp/device/views/default.ejs").url, self.options, function(el) {
								var el = self.element.find(".modal");
								$el = $(el);
								$el.modal('show');
								$el.on('hidden', function(){
									self.element.html('');
									self.destroy();
								});
							});
						};
				
				if (parseInt(self.options["id"]) > 0) {
					$.when(jsRoutes.controllers.DeviceType.index().ajax({
						headers: { 
					        Accept : "application/json; charset=utf-8",
					        "Content-Type": "application/json; charset=utf-8"
					    }}),
					    jsRoutes.controllers.DeviceGroup.index().ajax({
						headers: { 
					        Accept : "application/json; charset=utf-8",
					        "Content-Type": "application/json; charset=utf-8"
					    }}),
					    jsRoutes.controllers.Device.get(self.options["id"]).ajax({
						headers: { 
					        Accept : "application/json; charset=utf-8",
					        "Content-Type": "application/json; charset=utf-8"
					    }})).done(function(dt, dg, di) {
					    	$.extend(self.options, {"types": dt[0]});
					    	$.extend(self.options, {"groups": dg[0]});
					    	$.extend(self.options, di[0]);
					    	renderForm();
					    });
				} else {
					$.when(jsRoutes.controllers.DeviceType.index().ajax({
						headers: { 
					        Accept : "application/json; charset=utf-8",
					        "Content-Type": "application/json; charset=utf-8"
					    }}),
					    jsRoutes.controllers.DeviceGroup.index().ajax({
						headers: { 
					        Accept : "application/json; charset=utf-8",
					        "Content-Type": "application/json; charset=utf-8"
					    }})).done(function(dt, dg) {
					    	$.extend(self.options, {"types": dt[0]});
					    	$.extend(self.options, {"groups": dg[0]});
					    	renderForm();
					    });
				}
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
			
			".btn.device-create click": function(el, ev) {
				var self = this;

				jsRoutes.controllers.Device.create().ajax({
					data: self.element.find('form').serialize(),
					success: function(data, txtStatus, jqXHR) {
						location = jsRoutes.controllers.Device.index().url;
					},
					error: self.proxy(self._reportError)
				});
			},

			".btn.device-update click": function(el, ev) {
				var self = this;

				jsRoutes.controllers.Device.update(self.options.id).ajax({
					data: self.element.find('form').serialize(),
					success: function(data, txtStatus, jqXHR) {
						location = jsRoutes.controllers.Device.index().url;
					},
					error: self.proxy(self._reportError)
				});
			},

			prova : function() {
				var self = this;
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