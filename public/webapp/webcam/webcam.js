var GLOBAL_Webcam_LastId = 0;


steal('./jwplayer.js',
	  './jwplayerhead.js')
.then(
	function($){

		/**
		 * @class Webapp.webcam
		 */
		Aria.Controller('Webapp.webcam',
		/** @Static */
		{
			defaults : {
				id : -1,
				width : 528,
				height: 396,
				image: 'bg.jpg',
				autostart: true,
				file: 'rtmp://nox02.prod.nexusat.it/myapp?carg=1/mystream3?sarg=2',
				lastId : 0
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('webcam');

                self.options.id = self._getWebcamId();
                /*
				if (self.options.id == -1) {
					self.options.id = self._getWebcamId();
				}
				*/
				
				self.element.html('/assets/webapp/webcam/views/webcam.ejs' , { 'id' : self.options.id , 'file' : self.options.file } , function() {
					jwplayer('containerWebcam' + self.options.id).setup({
					    sources: [
					        {
					            file: self.options.file
					        }
					    ],
				    	//image: self.options.image,
					    autostart: self.options.autostart,
					    width: self.options.width,
					    controls: true,
					    height: self.options.height,
					    primary: 'flash',
					    rtmp: {
					    	bufferlength: 0.01
					    }
					});		

				});
			},
			
			".btn.command click" :  function (el, ev) {
		
				var self = this;
				var $el = $(el);
				var cmdName = $el.attr("data-command-name");
				var cmdArgs = $el.attr("data-command-args");
				var cmdConfirm = $el.attr("data-command-confirm");
				
				var showAlertInfo = function(isSuccess, message) {
					
					var el = self.element.parents()[2];
					el = $(el).find('.alerts');
					
					var html = $('<div class="alert ' + (isSuccess ? 'alert-success' : 'alert-error') + ' fade in">'+
							     '<button type="button" class="close" data-dismiss="alert">x</button>'+
							     '<div class="message">'+message+'</div></div>');
					html.appendTo(el);
					if (isSuccess) window.setTimeout(function() {html.alert('close');}, 3000); //autoclose success info
				}
				
				this.pendingCommand = function () {
					var self = this;
					var command = this._buildCommand(cmdName, cmdArgs);
					console.log("command [" + JSON.stringify(command) + "]");
					
					//showAlertInfo(true, "ciso" );
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
			},
			
			_buildCommand : function (command, args) {
				return {
				  device: this.options.device,
				  //tranId: this._createUUID(),
				  command: command,
				  sent_time: new Date(),
				  arguments : args === undefined ? [] : args
				};
			},

			_getWebcamId : function() {
				return parseInt(GLOBAL_Webcam_LastId++);
			}

		});

});