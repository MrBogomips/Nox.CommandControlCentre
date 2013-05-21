function init_map() {
    zoom      = 15;

    projLonLat   = new OpenLayers.Projection("EPSG:4326");   // WGS 1984
    projMercator = new OpenLayers.Projection("EPSG:900913"); // Spherical Mercator
    
    extentMax = 20037508.34;

    overviewMap = new OpenLayers.Control.OverviewMap();

    scale = new OpenLayers.Control.ScaleLine();
    scale.geodesic = true; // get the scale projection right, at least on small zoom levels

   
    map = new OpenLayers.Map("basicMap", 
                             { theme:    null,
                               controls: [ new OpenLayers.Control.Navigation(),    // direct panning via mouse drag
                                           new OpenLayers.Control.LayerSwitcher(), // select map and features to display
                                           new OpenLayers.Control.Attribution(),   // attribution text
                                           new OpenLayers.Control.PanZoomBar(),    // larger navigation control
                                           new OpenLayers.Control.Permalink(),     // bookmarkable map links
                                           scale,                                  // scale ruler
                                           overviewMap                             // overview map
                                         ],
                           maxExtent: new OpenLayers.Bounds(-extentMax, -extentMax, extentMax, extentMax)
                             } 
                            );

    
    map.addLayer(new OpenLayers.Layer.OSM.Mapnik("Mapnik"));
    
    map.addLayer(new OpenLayers.Layer.Google("Google (Map)",
        { 'type': google.maps.MapTypeId.ROADMAP, 'sphericalMercator': true } ));
    map.addLayer(new OpenLayers.Layer.Google("Google (Hybrid)",
        { 'type': google.maps.MapTypeId.HYBRID, 'sphericalMercator': true } ));
    map.addLayer(new OpenLayers.Layer.Google("Google (Satellite)",
        { 'type': google.maps.MapTypeId.SATELLITE, 'sphericalMercator': true } ));
    
    
    var mapCenterposition = new OpenLayers.LonLat(-40.324045,-20.262663).transform(projLonLat, projMercator);
    
    map.setCenter(mapCenterposition, zoom);
    overviewMap.maximizeControl();

	markerLayer = new OpenLayers.Layer.Markers("Devices");
	map.addLayer(markerLayer);
    
  /*
    oll_markers = new OpenLayers.Layer.Markers("Markers");
    map.addLayer(oll_markers);
    */	

}


