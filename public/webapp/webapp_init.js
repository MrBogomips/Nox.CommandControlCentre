// Put here common initialization required by the application and Aria Controllers.
// In particular place here your stealcommon assets loading
// steal();

steal('aria/page','webapp/fixtures').then(function() {
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
				}
			},
			fixtures: {
				query_string_pattern: '', //regular expression
			}
		};
	
	Aria.Page.getInstance(config);
}
);