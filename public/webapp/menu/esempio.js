{
	menu :	[
							{ 
								'type' : 'text' , 
								'enable' : true , 
								'button' : { 
									'icon' : 'icon-globe' , 
								  	'label' : 'Maps' , 
								  	'uri' : '#' , 
								  	'sub' : [ 
									  			{ 'type' : 'text' , 'enable' : true , 'button' : { 'label' : 'Realtime map' , 'uri' : 'map' } } , 
									  			{ 'type' : 'text' , 'enable' : true , 'button' : { 'label' : 'Hot zones' , 'uri' : '' } } , 
									  			{ 'type' : 'divider' } , 
									  			{ 'type' : 'text' , 'enable' : true , 'button' : { 'icon' : 'icon-cog' , 'label' : 'Configure map services' , 'uri' : '' } } 
								  		    ] 
								}
							} ,
							{ 
								'type' : 'text' , 
								'enable' : true , 
								'button' : { 
									'icon' : 'icon-share' , 
								  	'label' : 'Share' , 
								  	'uri' : '#' , 
								  	'sub' : [ 
									  			{ 'type' : 'text' , 'enable' : false , 'button' : { 'label' : 'Current view' , 'uri' : '' } } , 
									  			{ 'type' : 'text' , 'enable' : false , 'button' : { 'label' : 'PDF of current view' , 'uri' : '' } } , 
									  			{ 'type' : 'divider' } , 
									  			{ 'type' : 'text' , 'enable' : false , 'button' : { 'icon' : 'icon-cog' , 'label' : 'Configure sharing services' , 'uri' : '' } } 
								  		    ] 
								}
							} ,
							{ 
								'type' : 'text' , 
								'enable' : true , 
								'button' : { 
									'icon' : 'icon-hdd' , 
								  	'label' : 'Devices' , 
								  	'uri' : '#' , 
								  	'sub' : [ 
									  			{ 'type' : 'text' , 'enable' : true , 'button' : { 'label' : 'Devices' , 'uri' : jsRoutes.controllers.Device.index().url } } , 
									  			{ 'type' : 'text' , 'enable' : true , 'button' : { 'label' : 'Device types' , 'uri' : jsRoutes.controllers.DeviceType.index().url } } , 
									  			{ 'type' : 'text' , 'enable' : true , 'button' : { 'label' : 'Device groups' , 'uri' : jsRoutes.controllers.DeviceGroup.index().url } }
								  		    ] 
								}
							} ,
							{ 
								'type' : 'text' , 
								'enable' : true , 
								'button' : { 
									'icon' : 'icon-road' , 
								  	'label' : 'Vehicles' , 
								  	'uri' : '#' , 
								  	'sub' : [ 
									  			{ 'type' : 'text' , 'enable' : true , 'button' : { 'label' : 'Vehicles' , 'uri' : jsRoutes.controllers.Vehicle.index().url } } , 
									  			{ 'type' : 'text' , 'enable' : true , 'button' : { 'label' : 'Drivers' , 'uri' : jsRoutes.controllers.Driver.index().url } } , 
									  			{ 'type' : 'text' , 'enable' : true , 'button' : { 'label' : 'Vehicles assignements' , 'uri' : jsRoutes.controllers.Simcard.index().url } }
								  		    ] 
								}
							}
						]
}