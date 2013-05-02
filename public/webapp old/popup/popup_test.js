steal('funcunit').then(function(){

module("Webapp.popup", { 
	setup: function(){
		S.open("//webapp/popup/popup.html");
	}
});

test("Text Test", function(){
	equals(S("h1").text(), "Webapp.popup Demo","demo text");
});


});