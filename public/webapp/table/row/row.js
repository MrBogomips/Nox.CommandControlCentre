steal( '/assets/webapp/table/row/device_info/device_info.js',
	   '/assets/webapp/table/row/device_settings/device_settings.js',
	   '/assets/webapp/table/row/device_commandlog/device_commandlog.js',
	   '/assets/webapp/table/row/device_webcam/device_webcam.js',
	function($){

		/**
		 * @class Webapp.row
		 */
		Aria.Controller('Webapp.row',
		/** @Static */
		{
			defaults : {
				id : '',
				values : '',
				show_tools : true,
				show_webcam : true
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				this._super();
				this.element.addClass('webapp_table_row');
				this.data = this.options;
				this.commandQueue = {txs: {}, device: this.data.device};
				this.element.html('/assets/webapp/table/row/views/row2.ejs', self.options );
				this.sessionstart = 0;//this.options.times.sessionstart;
				this.frequencyCount = 0;
				this.frequency = "";
				this.data.times.lasttime = new Date();
				
				setInterval(function() { 
					self.frequency = self.frequencyCount;
					self.frequencyCount = 0;
					
					//frequency
					var e = $(self.element.find(".frequency:eq(0)"));
					e.html(self.frequency+"Hz");
				}, 1000);				
			} ,

			'.tool mousein' : function(el, ev) {
				$(el).tooltip('show');
			} ,

			'.tool mouseout' : function(el, ev) {
				$(el).tooltip('hide');
			} ,

			'.btn.settings click' : function(el, ev) {
				var self = this.data;
				$.extend(self, { 'parent' : this });
				var ai = this.element.find('.anchorInfo'); 
				ai.webapp_device_settings(self);
			} ,
			
			'.btn.more click' : function(el, ev) {
				var self = this.options;
				$.extend(self, { 'parent' : this });
				var ai = this.element.find('.anchorInfo');
				ai.webapp_device_info(self);
			//	alert('stop');
			} ,

			'.btn.webcam click' : function(el, ev) {
				var ai = this.element.find('.anchorInfo'); 
				ai.webapp_device_webcam({ 'deviceName' : this.options.device , 'webcams' : this.options.webcams });
			} ,
			
			'.label.event-command-counter click' : function(el, ev) {
				var commandQueue = $(el).parent().parent().parent().controller().commandQueue;
				commandQueue.device = this.data.device;
				var ai = this.element.find('.anchorInfo'); 
				ai.webapp_device_commandlog(commandQueue);
			} ,

			'#tblChannelSettings .btn.unsubscribe click' : function(el, ev) {
				var that = this;
				var channel = $.trim($(el).closest('tr').find('td.string').html());
				var channels = $('#channels').controller().channels;
				for (var i = 0; i < channels.length; i++) {
					if (channels[i].value == channel) {
						$('#channels').controller().channels[i].subscribed = false;
						$(el).closest('tr').remove();
					}
				}
			},
			// Check if the passed data is older than the current
			checkOldData : function(data) {
				return this.data.data.ts > data.data.ts;
			},
			
			updateData : function(data) {
				var data_type = data.message_subtype
				switch (data_type) {
				case "info":
					//this.data.times = { 'lasttime' : { 'day' : now.getDate() , 'month' : now.getMonth() , 'year' : now.getYear() , 'hours' : now.getHours() , 'minutes' : now.getMinutes() , 'seconds' : now.getSeconds() } };
					var now = new Date();
					var dateData = new Date(data.data.ts)
					var delay = now - new Date(dateData.getTime() - dateData.getTimezoneOffset() * 60 * 1000);
					var interval = now - this.data.times.lasttime;
					this.frequencyCount++;
					data.times = {};
					data.times.lasttime = now;
					data.times.interval = interval;
					data.times.delay = delay;
					this.data = data;
					
					//counter event
					var e = $(this.element.find(".counter:eq(0)"));
					e.html(parseInt(e.html()) + 1);
					//ignition		
					var e = $(this.element.find(".ignition:eq(0)"));
					if (data.data.objs.ignition == 1) {
						e.html("on")
						 .addClass("label-success")
						 .removeClass("label-important");
					} else {
						e.html("off")
						 .addClass("label-important")
						 .removeClass("label-success");
					}
					//moving
					var e = $(this.element.find(".moving:eq(0)"));
					if (data.data.objs.ignition == 1) {
						e.html("moving")
						 .addClass("label-success")
						 .removeClass("label-important");
					} else {
						e.html("stop")
						 .addClass("label-important")
						 .removeClass("label-success");
					}
					//speed
					var e = $(this.element.find(".speed:eq(0)"));
					e.html(data.data.speed+"Km/h");
					
					//g_force
					var e = $(this.element.find(".gforce:eq(0)"));
					var textGForce = "-";
					if (data.data.objs.g_force != undefined)
						textGForce = "x: " + (Math.round(data.data.objs.g_force[0] * 1000)/1000) + "<br>y: " + (Math.round(data.data.objs.g_force[1] * 1000)/1000) + "<br>z: " + (Math.round(data.data.objs.g_force[2] * 1000)/1000);
					e.html(textGForce);
					//ypr
					var e = $(this.element.find(".ypr:eq(0)"));
					var textYpr = "-";
					if (data.data.objs.ypr != undefined)
						textYpr = "y: " +(Math.round(data.data.objs.ypr[0] * 1000)/1000) + "<br>p: " + (Math.round(data.data.objs.ypr[1] * 1000)/1000) + "<br>r: " + (Math.round(data.data.objs.ypr[2] * 1000)/1000);
					e.html(textYpr);
					
					
					//sessionstart
					if (this.sessionstart == 0) {
						var e = $(this.element.find(".sessionstart:eq(0)"));
						e.html(dateData.toLocaleTimeString());
						this.sessionstart = 1;
					}
					
					//interval
					var e = $(this.element.find(".interval:eq(0)"));
					e.html(interval+"ms");
					//delay
					var e = $(this.element.find(".delay:eq(0)"));
					e.html(delay+"ms");
					//led							
					var e = $(this.element.find(".led_on:eq(0)"));
					if (data.data.objs.hasOwnProperty("led_on")) {
						if (data.data.objs.led_on == 1) {
							e.html("on")
							 .addClass("label-success")
							 .removeClass("label-important");
						} else {
							e.html("off")
							 .addClass("label-important")
							 .removeClass("label-success");
						}						
					}
					//led_backup	
					var e = $(this.element.find(".led_backup:eq(0)"));
					if (data.data.objs.hasOwnProperty("led_backup")) {
						if (data.data.objs.led_backup == 1) {
							e.html("on")
							 .addClass("label-success")
							 .removeClass("label-important");
						} else {
							e.html("off")
							 .addClass("label-important")
							 .removeClass("label-success");
						}
					}
					
					//people_counter
					if (data.data.objs.hasOwnProperty("people_counter")) {
						for (var i = 0; i < data.data.objs.people_counter.length; i++) {
							var port = data.data.objs.people_counter[i];
							var e = $(this.element.find(".port" + port.door + ":eq(0)"));

							switch (port.status) {
								case "WRN_DARK": //anomalia
									e.find(".status").html("WARNING_DARK").css("background-color", "yellow");
									break;
								case "ERR_COVERED": //anomalia
									e.find(".status").html("ERROR_COVERED").css("background-color", "yellow");
									break;
								case "WRN_BRIGHT": //anomalia
									e.find(".status").html("WARNING_BRIGHT").css("background-color", "yellow");
									break;
								case "OK": //status OK
									if (port.door_sts == "DOOR_OPEN") {
										e.find(".status").css("background-color", "green");
									} else {
										e.find(".status").css("background-color", "red");
									}
									break;
							}

							e.find(".cnt_out_img").html("<img alt=\"\" src=\"assets/img/freccia.svg\" />");
							e.find(".cnt_in_img").html("<img alt=\"\" src=\"assets/img/freccia.svg\" />");
							if (port.msg_type == "boarding") {
								e.find(".cnt_out").html(port.tmp_cnt_out);
								e.find(".cnt_in").html(port.tmp_cnt_in);
							} else { //definitive
								e.find(".cnt_out").html(port.cnt_out);
								e.find(".cnt_in").html(port.cnt_in);
							}
						}
					}
					
					break;
				case "position":
					alert("controller row: TODO: update position data only");
					break;
				case "commandRequest":
				case "commandResponse":
					var e = $(this.element.find(".event-command-counter:eq(0)"));
					if (e.html() == "0/0") e.css("visibility", "visible");
					var cs = e.html().split("/");
					if (this.commandQueue.txs[data.tranId] === undefined)
						this.commandQueue.txs[data.tranId] = {request: null, responses: []};
					if (data_type == "commandRequest") {
						cs[0] = parseInt(cs[0]) + 1;
						this.commandQueue.txs[data.tranId].request = data;
					}
					else {
						cs[1] = parseInt(cs[1]) + 1;
						this.commandQueue.txs[data.tranId].responses.push(data);
						if (data.exitstatus == "FAILED")
							e.addClass("label-important");
					}
					e.html(cs.join("/"));
					break;
				}
			} ,

			'.devicename click' : function(el, ev) {
					var name = $(el).attr('name');
					$(".leaflet-marker-icon[title='" + name + "']").click().mouseup();
			} ,

			_getDeviceInfo : function(options){
				var self = this;
				return $.Deferred(
					function(deferred) {
						if (options.info != undefined) {
							$.when(
								self._getDeviceInfoValues(options, options.info.deviceGroupId, jsRoutes.controllers.DeviceGroup, 'deviceGroup'),
								self._getDeviceInfoValues(options, options.info.deviceTypeId, jsRoutes.controllers.DeviceType, 'deviceType'),
								self._getDeviceInfoValues(options, options.info.vehicleId, jsRoutes.controllers.Vehicle, 'vehicle'),
								self._getDeviceInfoValues(options, options.info.simcardId, jsRoutes.controllers.Simcard, 'simcard')
							).always(
								function() {
							    	deferred.resolve();
							    }
							);
						}
						else {
							deferred.resolve();
						}
					}).promise();
			},

			_getDeviceInfoValues : function(options, id, controllerClass, key) {
				var self = this;
				return $.Deferred(
					function(deferred) {
						if (id != null) {
							controllerClass.get(id).ajax({
								headers: { 
								    Accept : "application/json; charset=utf-8",
								    "Content-Type": "application/json; charset=utf-8"
								}}
							).done(
								function(data) {
							    	options.info[key] = data;
							    }
							).fail(
								function (jqXHR, textStatus) {
									
								}
							).always(
								function() {
							    	deferred.resolve();
							    }
							);
						}
						else {
							deferred.resolve();
						}
					}).promise();
			}

		});

});