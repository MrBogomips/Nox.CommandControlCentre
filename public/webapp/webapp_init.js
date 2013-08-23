// Put here common initialization required by the application and Aria Controllers.
// In particular place here your stealcommon assets loading
// steal();

steal('aria/page')
.then(function() {
	var config = {
			localization: {
				language: '',
				formats: {
					date: '',
					time: '',
					decimal_separator: '',
					thousands_separator: ''
				}
			},
			service: {
				bases: {
					UrlBase: ''
				},
				fixtures: {
					query_string_pattern: '', //regular expression
					enable: true
				}
			}

		};
	
	Aria.Page.getInstance(config);
	})
.then('/assets/aria/aria/controller/controller',
	  '/assets/aria/jquery/view/ejs/ejs',
	  '/assets/javascripts/underscore.js',
	  '/assets/js/routes.js',
	  '/assets/javascripts/bootstrap.js',
	  '/assets/javascripts/bootstrap-modalmanager.js',
	  '/assets/javascripts/bootstrap-datepicker.js',
	  '/assets/javascripts/bootstrap-switch.js',
	  '/assets/javascripts/bootstrap-select.js')
.then('/assets/javascripts/bootstrap-modal.js',
	  '/assets/webapp/modalform/modalform.js',
	  '/assets/javascripts/jquery-blockui.js')
.then('/assets/javascripts/defaults.js',
	function() {
	$.ajax({
		   type: 'GET',
		    url: '/app/configuration',
		    async: false,
		    contentType: "application/json",
		    dataType: 'json',
		    success: function(json) {
		    	Aria.Page.getInstance().configuration = json;
		    },
		    error: function(e) {
		       alert("FATAL ERROR: unable to retrieve application configuration");
		    }
		});
});