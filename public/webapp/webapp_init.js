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
.then('/assets/aria/steal/less/less',
	  '/assets/aria/aria/controller/controller',
	  '/assets/aria/jquery/view/ejs/ejs',
	  '/assets/js/bootstrap.js',
	  '/assets/js/bootstrap-modalmanager.js',
	  '/assets/js/bootstrap-modal.js',
	  '/assets/css/bootstrap.css',
	  '/assets/css/bootstrap-responsive.css',
	  '/assets/css/bootstrap-modal.css',
	  '/assets/js/underscore.js'
	  )
.then('/assets/webapp/style/global.less'
		);