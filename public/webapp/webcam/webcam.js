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
				
				self.element.html('/assets/webapp/webcam/views/webcam.ejs' , { 'id' : self.options.id } ,
					function() {
						jwplayer('containerWebcam' + self.options.id).setup({
						    sources: [
						        {
						            file: self.options.file
						        }
						    ],
						//    image: self.options.image,
						    autostart: self.options.autostart,
						    width: self.options.width,
						    height: self.options.height,
						    primary: 'flash'
						});
					}
				);

					

			} ,

			_getWebcamId : function() {
				/*var i = 0;
				while($('#containerWebcam' + i).length > 0) {
					i += 1;
				}
				*/
                //self.defaults.lastId = self.defaults.lastId + 1;
				return "XXX" + GLOBAL_Webcam_LastId++;
			}

		});

});