// load('webapp/scripts/crawl.js')

load('steal/rhino/rhino.js')

steal('steal/html/crawl', function(){
  steal.html.crawl("webapp/webapp.html","webapp/out")
});
