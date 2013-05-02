steal('funcunit').then(function(){

module("Webapp.settings", { 
	setup: function(){
		S.open("//webapp/settings/settings.html");
	}
});

test("Text Test", function(){
	equals(S("h1").text(), "Webapp.settings Demo","demo text");
});


});