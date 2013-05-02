steal( '/assets/aria/steal/less/less',
		'/assets/aria/aria/controller/controller',
		'/assets/aria/jquery/view/ejs/ejs')
	.then( './views/init.ejs', './css/Webapp.HelloWorld.less', function($){

/**
 * @class Webapp.HelloWorld
 */
Aria.Controller('Webapp.HelloWorld',
/** @Static */
{
	defaults : {}
},
/** @Prototype */
{
	init : function(){
		this._super();
		this.element.addClass('webapp_hello_world');
		this.render({message: 'Hello Aria!'});
	}
});

});