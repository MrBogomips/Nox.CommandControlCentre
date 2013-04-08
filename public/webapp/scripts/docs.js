//js webapp/scripts/doc.js

load('steal/rhino/rhino.js');
steal("documentjs").then(function(){
	DocumentJS('webapp/webapp.html', {
		markdown : ['webapp']
	});
});