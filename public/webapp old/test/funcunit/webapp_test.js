steal("funcunit", function(){
	module("webapp test", { 
		setup: function(){
			S.open("//webapp/webapp.html");
		}
	});
	
	test("Copy Test", function(){
		equals(S("h1").text(), "Welcome to JavaScriptMVC 3.2!","welcome text");
	});
})