steal('funcunit').then(function(){

module("Webapp.channels.items", { 
	setup: function(){
		S.open("//webapp/channels/items/items.html");
	}
});

test("Text Test", function(){
	equals(S("h1").text(), "Webapp.channels.items Demo","demo text");
});


});