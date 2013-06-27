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
					self.element.html('/assets/webapp/device/views/default.ejs', self.options, function(el) {
						var el = self.element.find(".modal");//.find('.modal.commands');
						$el = $(el);
						$el.modal('show');
						$el.on('hidden', function(){
							self.element.html('');
							self.destroy();
						});
					});
				};
				
				$.getJSON('/device_types', function(data) {
					$.extend(self.options, data);
					$.getJSON('/device_groups', function(data) {
						$.extend(self.options, data);
						if (parseInt(self.options["id"]) > 0) {
							$.getJSON('/device/'+self.options["id"], function(data) {
								$.extend(self.options, data);
								renderForm();
							});
						} else {
							renderForm();
						}
					});
				});
				
			} ,
			destroy : function(){
				var self = this;
			    this._super();
			},
			
			".btn.device-create click": function(el, ev) {
				var self = this;
				var data = this.element.find('form').serialize();
				//alert(data);
				$.post('/device', data)
					.done(function(data, txtStatus, jqXHR) {
						/*alert(data);
						alert(txtStatus);
						alert(jqXHR);
						*/
						location.reload(true);
					})
					.fail(function(data, txtStatus, jqXHR) {
						var $alert= $("<div class='alert alert-block alert-error'><button type='button' class='close' data-dismiss='alert'>×</button><h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p></div>");
						self.find(".alert_placeholder").html($alert);
						//$alert.appendTo()
						
					});
			},
			".btn.device-update click": function(el, ev) {
				var self = this;
				var data = this.element.find('form').serialize();
				$.ajax({
					url: '/device/'+this.options.id, 
					data: data,
					type: 'PUT'
					})
				.done(function(data, txtStatus, jqXHR) {
					location.reload(true);
				})
				.fail(function(data, txtStatus, jqXHR) {
					var $alert= $("<div class='alert alert-block alert-error'><button type='button' class='close' data-dismiss='alert'>×</button><h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p></div>");
					self.find(".alert_placeholder").html($alert);
				});
			}
		});

});