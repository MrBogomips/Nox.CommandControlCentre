steal(
	function($){

		/**
		 * @class Webapp.vehiclesassignements
		 */
		Aria.Controller('Webapp.VehicleAssignements.Row',
		/** @Static */
		{
			defaults : {
				id: -1
			}
		},
		/** @Prototype */
		{
			init : function(options) {
				var self = this;
				this._super();
				this.element.addClass('webapp_vehiclesassignements_row');
				
				// set the containing table
				self.container = self.element.closest("table.vehicle-assignements");
				var nowTemp = new Date();
				var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);
				// select the view
				var view = '';
				if (self.options['id'] > -1) {
					view = "/assets/webapp/vehicleassignements/row/views/show.ejs";
				}
				else
				{
					view = "/assets/webapp/vehicleassignements/row/views/new.ejs"
				}
				// render the view
				self.element.html(view, self.options, function() {
					self.element.find(".switch").bootstrapSwitch();
					var beginDate = self.element.find(".date.beginAssignement").datepicker({
						onRender: function(date) {
						    return date.valueOf() < now.valueOf() ? 'disabled' : '';
						  }
					})
					.on('changeDate', function(ev){
						var endDate = self.element.find(".date.endAssignement").data('datepicker');
						var beginDate = self.element.find(".date.beginAssignement");
						if (ev.date.valueOf() > endDate.date.valueOf()) {
						    var newDate = new Date(ev.date)
						    newDate.setDate(newDate.getDate() + 1);
						    endDate.setValue(newDate);
						  } else {
							  endDate.setValue(endDate.date);  // force endDate redraw!!!
						  }
						  beginDate.datepicker('hide');
						  //endDate[0].focus();
					})
					.data('datepicker');
					var endDate = self.element.find(".date.endAssignement").datepicker({
						onRender: function(date) {
							return date.valueOf() <= beginDate.date.valueOf() ? 'disabled' : '';
						}
					}).on('changeDate', function(ev){
						var endDate = self.element.find(".date.endAssignement");
						endDate.datepicker('hide');
					});
					self.element.find(".selectpicker").selectpicker();
				});
				
			},

			destroy : function() {
				var self = this;
			    this._super();
			},

			_reportError : function(data, txtStatus, jqXHR) {
				var self = this;
				var errors = $.parseJSON(data.responseText);
				self._enumerateErrors(errors);
			},

			_enumerateErrors : function(errors) {
				var self = this;
				if (errors == undefined) return;
				var globalErrors = '';
				var counter = 0;
				var gerrors = 0;
				$.each(errors, function(label, value) {
					var obj = self.find('input[name="' + label + '"],select[name="' + label + '"]');
					if (obj.length > 0) {
						obj.closest('.control-group').addClass('error');
						var errorsList = '';
						for (var i = 0; i < value.length; i++) {
							errorsList += '<li class="error-inline">' + value[i].replace('.', ' ') + '</li>';
						}
						if (errorsList != '') {
							obj.closest('.controls').append('<div class="help-inline"><ul class="error-inline">' + errorsList + '</ul></div>');
						}
					}
					else {
						for (var i = 0; i < value.length; i++) {
							globalErrors += value[i].replace('.', ' ') + '<br/>';
							gerrors += 1;
						}
					}
				});
				if (globalErrors != '') {
					var $alert= $('<a data-content="' + globalErrors + '" title="" data-toggle="popover" class="btn btn-danger errorcount" href="#" data-original-title="Errors">' + gerrors + '</a><div class="popover fade left"><div class="arrow"></div><div class="popover-content">' + globalErrors + 'ciao</div></div>');
					self.find(".colbtn nobr .insert-section").append($alert);
				}
			},

			_cancelErrors : function() {
				var self = this;
				self.find('.control-group').removeClass('error').find('.help-inline').remove();
				self.find('.insert-section').find('a[data-toggle="popover"]').remove();
			}, 
			
			'input[name="beginAssignement"] change' : function(el, ev) {
				return;
				var self = this;
				var $endAss = self.element.find('input[name="endAssignement"]')
				if (el.date.valueOf() > $endAss.date.valueOf()) {
				    $endAss.setValue(el.date);
				  }
			},
			
			'.btn.save click' : function(el, ev) {
				var self = this;
				
				el.button('loading');
				self.container.block();
				
				jsRoutes.controllers.VehicleAssignement.create().ajax({
					data: self.element.find('select, input').serialize()
				})
				.done(function(data, txtStatus, jqXHR) {
					// SUCCESS
					self.options.id = data.id;
					self.element.find("td.assignement-id").html(data.id);
					self.element.find('input[name="id"]').val(data.id);
					self.element.find("div.insert-section").hide();
					self.element.find("div.update-section").show();
					self.container.parent().find("button.create").removeClass('disabled');
					
					self._cancelErrors();
					//el.button('reset');
					setTimeout(function() {
					    self.element.find('button.update').addClass('disabled');
					}, 100);
				})
				.fail(function(data, txtStatus, jqXHR) {
					// FAILURE
					// console.log("ERRORE!!!");
					self.proxy(self._reportError(data, txtStatus, jqXHR));
				})
				.always(function(){
					el.button('reset');
					self.container.unblock();
				});
			},
			
			'input,select change' : function() {
				this.element.find('button.update').removeClass('disabled');
			},
			
			'.btn.update click' : function (el, ev) {
				if (el.hasClass('disabled')) return;
				var self = this;
				
				el.button('loading');
				self.container.block();
				
				jsRoutes.controllers.VehicleAssignement.update(self.options.id).ajax({
					data: self.element.find('select, input').serialize()
				})
				.done(function(data, txtStatus, jqXHR) {
					// SUCCESS
					el.button('reset');
					$version = self.element.find('input[name="version"]');
					$version.attr('value', 1 + parseInt($version.attr('value')));
					setTimeout(function() {
					    el.addClass('disabled');
					}, 0);
				})
				.fail(function() {
					// FAILURE
					el.button('reset');
				})
				.always(function(){
					self.container.unblock();
				});
			},
			
			'.btn.delete click' : function (el, ev) {
				var self = this;
				
				el.button('loading');
				self.container.block();
				
				jsRoutes.controllers.VehicleAssignement.delete(self.options.id).ajax({
					data: self.element.find('select, input').serialize()
				})
				.done(function(data, txtStatus, jqXHR) {
					self.element.remove();
				})
				.fail(function() {
					// FAILURE
				})
				.always(function(){
					el.button('reset');
					self.container.unblock();
				});
			},

			'.errorcount click' : function (el, ev) {
				var self = this;
				
				$(el).popover('toggle');
			}
		});

});