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
				this.element.addClass('device_settings');
				this.element.html('/assets/webapp/table/row/device_settings/views/view.ejs', self.options, function(el) {
					var el = self.element.find('.modal.commands');
					$el = $(el);
					$el.modal({remote: '/device/'+ self.options.device +'/configure'});
					$el.on('hidden', function(){
						self.element.html('');
						self.destroy();
					});
				});
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
						this.element.find('.modal.confirmation').modal({replace: true});
					} else {
						this.pendingCommand();
					}
				} else {
					
				}
				//this.pendingCommand = undefined;
			},
			
			".btn.cancel click" :  function () {
				this.pendingCommand = undefined;
			},
			
			".btn.confirm click" :  function () {
				this.pendingCommand();
				this.pendingCommand = undefined;
			},
			
			/*
			_createUUID: function (){
				return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
				    var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
				    return v.toString(16);
				});
			},
			*/ 
			
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