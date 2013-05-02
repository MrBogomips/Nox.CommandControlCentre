steal('funcunit').then(function(){

module("Webapp.checkbox", { 
	setup: function(){
		S.open("//webapp/checkbox/checkbox.html");
	}
});

test("Text Test", function(){
	equals(S("h1").text(), "Webapp.checkbox Demo","demo text");
});


});