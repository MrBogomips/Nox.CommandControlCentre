steal(
	function($){

		/**
		 * @class Webapp.row
		 */
		Aria.Controller('Webapp.device_settings',
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
				this.element.addClass('device_settings modal hide fade commands');
				
				this.element.attr("tabindex", "-1").attr("aria-hidden", "true");
				

				$.when(
						self.options.parent._getDeviceInfo(self.options)
					).then(
						function() {
							if (self.options.info == undefined) {
								self.options.info = null;
							}
							self.element.html('/assets/webapp/table/row/device_settings/views/view.ejs', self.options, function(el) {
								//var el = self.element.find('.modal.commands');
								var el = self.element;
								$el = $(el);
								$el.modal({remote: '/device/'+ self.options.device +'/configure'});
								$el.on('hidden', function(){
									self.element.html('');
									self.destroy();
								});
							});
						
						//	self.device = self.options.device;
						}
					)
				
				
				
			}, 
			
			".btn.command click" : function (el, ev) {
				var self = this;
				var $el = $(el);
				var cmdName = $el.attr("data-command-name");
				var cmdArgs = $el.attr("data-command-args");
				var cmdConfirm = $el.attr("data-command-confirm");
				
				var showAlertInfo = function(isSuccess, message) {
					var el = self.element.find('.alerts');
					
					var html = $('<div class="alert ' + (isSuccess ? 'alert-success' : 'alert-error') + ' fade in">'+
							     '<button type="button" class="close" data-dismiss="alert">x</button>'+
							     '<div class="message">'+message+'</div></div>');
					html.appendTo(el);
					if (isSuccess) window.setTimeout(function() {html.alert('close');}, 3000); //autoclose success info
				}
				
				this.pendingCommand = function () {
						var self = this;
						var command = this._buildCommand(cmdName, cmdArgs);
						$.ajax({
						 type: "POST",
						 url: "/device/"+this.options.device+"/execute",
					     contentType: "application/json; charset=UTF-8",
						 dataType: "json",
						 data: JSON.stringify(command),
						 success: function(data,textStatus,jqXHR){
							 showAlertInfo(data.status != "ERR", "Command ["+data.tranId+"]: " + data.description );
							 data.device = self.options.device;
							 data.message_type = "tracking";
							 data.message_subtype = "commandRequest";
							 data.command = command;
							 var app = Aria.Page.getInstance();
							 var ch = app.getChannelByName("tracking");
							 ch.trigger(data.message_subtype, data);
						},
						 error: function(jqXHR,textStatus,errorThrown){
							 var msg = "HTTP-" + jqXHR.status;
							 msg += ": " + jqXHR.statusText + "<br>";
							 msg += "Server response: " + jqXHR.responseText;
							 showAlertInfo(false, msg);
						 }
					});
				}
				
				if (cmdArgs === undefined) {
					if (cmdConfirm !== undefined) {
						if (confirm("Dow you want proceed?"))
							this.pendingCommand();
					} else {
						this.pendingCommand();
					}
				} else {
					
				}
				//this.pendingCommand = undefined;
			},
			
			_renderConfirmationModal : function () {
				return '<div class="modal hide fade confirmation" tabindex="-1">' +
				  +'<div class="modal-header">'  
				  + '<h3>Confirmation</h3>'
				  + '</div>'
				  + '<div class="modal-body">'
				  + '  Do you confirm the execution of the command?'
				  + '</div>'
				  + '<div class="modal-footer">'
				  + '  <button class="btn btn-danger cancel" data-dismiss="modal" aria-hidden="true">Cancel</button>'
				  + '  <button class="btn btn-success confirm" data-dismiss="modal" aria-hidden="true">Confirm</button>'
				  + '</div>'
			},
			
			".btn.cancel click" :  function () {
				this.pendingCommand = undefined;
				this.confirmationModal.modal('hide');
			},
			
			".btn.confirm click" :  function () {
				this.pendingCommand();
				this.pendingCommand = undefined;
				this.confirmationModal.modal('hide');//.destroy();
			},
			
			_buildCommand : function (command, args) {
				return {
				  device: this.options.device,
				  //tranId: this._createUUID(),
				  command: command,
				  sent_time: new Date(),
				  arguments : args === undefined ? [] : args
				};
			}
		});

});