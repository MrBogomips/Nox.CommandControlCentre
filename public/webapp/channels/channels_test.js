steal('funcunit').then(function(){

module("Webapp.channels", { 
	setup: function(){
		S.open("//webapp/channels/channels.html");
	}
});

test("Text Test", function(){
	equals(S("h1").text(), "Webapp.channels Demo","demo text");
});


});