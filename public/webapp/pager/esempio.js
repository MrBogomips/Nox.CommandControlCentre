{
'datasource' : { 'uri' : '/device/index' } ,
'header': { 'title' : 'Devices' , 'render': fn() } ,
'footer': { 'render': fn() } ,
'paging': { 'page_size' : 10 , 'page_index_init' : 0 } ,
'searching': { 'show': true } ,
'columns' : [ { 'title' : 'ID' , 'map' : 'Id' } , { 'title' : 'Device ID', 'map' : 'displayName' } , { 'title' : '' , 'map' : function() { $(this).html('...'); } } ] }




/// ESEMPIO D'USO

$('...').webapp_pager({...})
