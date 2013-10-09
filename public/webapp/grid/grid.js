steal('/assets/webapp/grid/search/search.js',
	  '/assets/webapp/grid/sorter/sorter.js',
	  '/assets/webapp/grid/global/global.js',
	  '/assets/webapp/grid/paginator/paginator.js',
	  '/assets/webapp/grid/tablegrid/tablegrid.js')
.then(
	function($){

		/**
		 * @class Webapp.grid
		 */
		Aria.Controller('Webapp.grid',
		/** @Static */
		{
			defaults : {
				parametres : {
					q : '' ,
					xq : '' ,
					s : '' ,
					pz : '' ,
					px : ''
				} ,
				model : webapp.grid ,
				
			}
		},
		/** @Prototype */
		{
			init : function() {
					var self = this;
					this._super();
					this.element.addClass('webapp_grid');
					
					var sorting = { 'opt_keys' : [ { 'label' : 'Name' , 'key' : 'name' } , { 'label' : 'Model' , 'key' : 'model' } , { 'label' : 'Display Name' , 'key' : 'displayName' } ] , 'default_criteria' : [ { 'key' : 'name' , 'direction' : '+' } , { 'key' : 'model' , 'direction' : '-' } ] };
					var pagination = { 'opt_size' : [ { 'label' : '5 elements' , 'value' : 5 } , { 'label' : '10 elements' , 'value' : 10 } , { 'label' : '20 elements' , 'value' : 20 } ] , 'default_size' : 5 , 'default_page' : 1 };
					var show_refresh_button = true;
					var global_actions = [ { 'label' : 'Create new vehicle' , 'action_fn' : function(gridcontroller) {  } } , { 'label' : 'Global action #2' , 'action_fn' : function(gridcontroller) {  } } ];
					var header = {};
					var element = {};


					self.element.html('/assets/webapp/grid/views/grid.ejs', { 'parent' : self , 'sorting' : sorting , 'show_refresh_button' : show_refresh_button , 'global_actions' : global_actions , 'pagination' : pagination , 'header' : header , 'element' : element });
				} ,
			
			destroy : function(){
					this.element.html('');
				    this._super();
				} ,

			_callData : function(q, xq, s, pz, px) {
					var self = this;
					self._manageData(q, xq, s, pz, px);
					return $.Deferred(
						function(deferred) {
							self.options.models.findAll(
								{ 'q' : self.options.q , 'xq' : self.options.xq , 's' : self.options.s , 'pz' : self.options.pz , 'xq' : self.options.px },
								function(data) {
							    	self.element.find('.tablegrid').controller()._managerecords(data);
							    	deferred.resolve();
							    },
								function (jqXHR, textStatus) {
									alert(textStatus.responseText);
									deferred.resolve();
								}
							);
						}).promise();
				} ,

			_manageData : function(q, xq, s, pz, px) {
					var self = this;
					self.options.q = q == undefined ? self.options.q : q;
					self.options.xq = xq == undefined ? self.options.xq : xq;
					self.options.s = s == undefined ? self.options.s : s;
					self.options.pz = pz == undefined ? self.options.pz : pz;
					self.options.px = px == undefined ? self.options.px : px;
				}

		});

});