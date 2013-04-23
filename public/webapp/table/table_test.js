steal('funcunit').then(function(){

module("Webapp.settings.table", { 
	setup: function(){
		S.open("//webapp/settings/table/table.html");
	}
});

test("Text Test", function(){
	equals(S("h1").text(), "Webapp.settings.table Demo","demo text");
});


});