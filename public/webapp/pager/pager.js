steal(
	function($){

		/**
		 * @class Webapp.Pager
		 */
		Aria.Controller('Webapp.Pager',
		/** @Static */
		{
			defaults : {
				'datasource' : { 'uri' : '' } ,
				'cssclass' : '' ,
				'header' : { 'title' : '' , 'render': null } ,
				'footer' : { 'render' : null } ,
				'paging' : { 'show' : false , 'size' : 10 , 'init' : 0 } ,
				'searching' : { 'show' : false } ,
				'coloumns' : [  ] , 
				'rows' : [  ]
			}
		},
		/** @Prototype */
		{
			init : function() {
				var self = this;
				self._super();
				self.element.addClass('webapp_pager');
				$.when(self._getRows())
				.then(
					function() {
						$.when(self._getView())
						.then(
							function() {
								self.find('.switch')['bootstrapSwitch']();
							}
						)
					}
				);
			},
			
			destroy : function() {
				var self = this;
			    this._super();
			},
			
			_getView : function() {
				var self = this;
				return $.Deferred(
					function(deferredx){
						self.element.html(jsRoutes.controllers.Assets.at("webapp/pager/views/default.ejs").url, self.options, function(el) { deferredx.resolve(); })
					}
				).promise();
			},
			
			_getRows : function() {
				var self = this;
				return $.Deferred(
					function(deferred){
						self.options.datasource.uri.index().ajax({
							headers: { 
						        Accept : "application/json; charset=utf-8",
						        "Content-Type": "application/json; charset=utf-8"
						    },
							success: function(data) {
								$.extend(self.options.rows, data);
								deferred.resolve();
							}
						});
					}
				).promise();
			}

		});

});