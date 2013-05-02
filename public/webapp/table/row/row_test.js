steal('funcunit').then(function(){

module("Webapp.table.row", { 
	setup: function(){
		S.open("//webapp/table/row/row.html");
	}
});

test("Text Test", function(){
	equals(S("h1").text(), "Webapp.table.row Demo","demo text");
});


});