steal(function($) {

	/**
	 * @class Webapp.table
	 */
	Webapp.BaseForm('Webapp.Driver',
	/** @Static */
	{
		defaults : {
			id : '',
			name : '',
			surname : '',
			display_name : '',
			creation_time : '',
			modification_time : '',
			enabled : true
		}
	},
	/** @Prototype */
	{
		init : function() {
			var self = this;
			this._super();
			this.element.addClass('webapp_driver');
			
			var renderForm = function() {
				self.element.html(jsRoutes.controllers.Assets.at("webapp/driver/views/default.ejs").url, self.options, 
					function(el) {
						self.element.find(".switch").bootstrapSwitch();
						var el = self.element.find(".modal");
						$el = $(el);
						$el.modal('show');
						$el.on('hidden', function() {
							self.element.html('');
							self.destroy();
						});
					});
			};

			if (parseInt(self.options["id"]) > 0) {
				jsRoutes.controllers.Driver.get(self.options["id"]).ajax({
					headers : {
						Accept : "application/json; charset=utf-8",
						"Content-Type" : "application/json; charset=utf-8"
					}
				}).done(function(data, dg, di) {
					$.extend(self.options, data);
					renderForm();
				});
			} else {
				renderForm();
			}
		},

		destroy : function() {
			var self = this;
			this._super();
		},

		'.btn.driver-create click' : function(el, ev) {
			var self = this;

			jsRoutes.controllers.Driver.create().ajax({
				data : self.element.find('form').serialize(),
				success : function(data, txtStatus, jqXHR) {
					location = jsRoutes.controllers.Driver.index().url;
				},
				error : self.proxy(self._reportError)
			});
		},

		'.btn.driver-update click' : function(el, ev) {
			var self = this;

			jsRoutes.controllers.Driver.update(self.options.id).ajax({
				data : self.element.find('form').serialize(),
				success : function(data, txtStatus, jqXHR) {
					location = jsRoutes.controllers.Driver.index().url;
				},
				error : self.proxy(self._reportError)
			});
		}
	});

});