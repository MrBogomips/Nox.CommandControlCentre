steal('funcunit').then(function(){

module("Webapp.HelloWorld", { 
	setup: function(){
		S.open("//webapp/hello_world/hello_world.html");
	}
});

test("Text Test", function(){
	equals(S("h1").text(), "Webapp.HelloWorld Demo","demo text");
});


});