@(user: UserTrait)
<div class="navbar">
	<div class="navbar navbar-static">
		<div class="navbar-inner">
			<div class="container" style="width: auto;">
				<a class="brand" href="#">Nox</a>

				<ul class="nav" role="navigation">
					<!-- 
					<li class="dropdown">
						<a id="drop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown"><i class="icon-large icon-home"></i> Org. #1<b class="caret"></b></a>
						<ul class="dropdown-menu" role="menu" aria-labelledby="drop1">
							<li><a tabindex="-1" href="#">Organization #2 (Customer A)</a></li>
							<li><a tabindex="-1" href="#">Organization #3 (Customer A)</a></li>
							<li><a tabindex="-1" href="#">Organization #4 (Customer B)</a></li>
							<li class="divider"></li>
							<li><a tabindex="-1" href="#"><i class="icon-cog"></i> Manage organizations</a></li>
						</ul>
					</li>
					-->

					@{/***** MAPS *****/}
					<li class="dropdown">
						<a href="#" id="dropMaps" role="button" class="dropdown-toggle" data-toggle="dropdown"><i class="icon-globe icon-white"></i> Maps<b class="caret"></b></a>
						<ul class="dropdown-menu" role="menu" aria-labelledby="drop2">
							<li><a tabindex="-1" href="@routes.Map.index">Realtime map</a></li>
							<li><a tabindex="-1" href="#">Hot zones</a></li>
							<li class="divider"></li>
							<li><a tabindex="-1" href="#"><i class="icon-cog"></i> Configure map services</a></li>
						</ul>
					</li>

					@{/***** SHARE *****/}
					<li class="dropdown">
						<a href="#" id="dropShare" role="button" class="dropdown-toggle" data-toggle="dropdown"><i class="icon-share icon-white"></i> Share<b class="caret"></b></a>
						<ul class="dropdown-menu" role="menu" aria-labelledby="drop3">
							<li><a tabindex="-1" href="#" class="workinprogress">Current view</a></li>
							<li><a tabindex="-1" href="#" class="workinprogress">PDF of current view</a></li>
							<li class="divider"></li>
							<li><a tabindex="-1" href="#" class="workinprogress"><i class="icon-cog"></i> Configure sharing services</a></li>
						</ul>
					</li>
					@{/***** DEVICES *****/}
					<li class="dropdown">
						<a href="#" id="dropDevices" role="button" class="dropdown-toggle" data-toggle="dropdown"><i class="icon-hdd icon-white"></i> Devices<b class="caret"></b></a>
						<ul class="dropdown-menu" role="menu" aria-labelledby="drop4">
							<li><a tabindex="-1" href="#" id="btnDevices">Devices</a></li>
							<li><a tabindex="-1" href="#" id="btnDeviceTypes">Device types</a></li>
							<li><a tabindex="-1" href="#" id="btnDeviceGroups">Device groups</a></li>
							<li><a tabindex="-1" href="#" id="btnSimcards">Simcards</a></li>
						</ul>
					</li>
					@{/***** VEHICLES *****/}
					<li class="dropdown">
						<a href="#" id="dropVehicle" role="button" class="dropdown-toggle" data-toggle="dropdown"><i class="icon-road icon-white"></i> Vehicles<b class="caret"></b></a>
						<ul class="dropdown-menu" role="menu" aria-labelledby="drop5">
							<li><a tabindex="-1" href="#" id="btnMenuVehicle">Vehicles</a></li>
							<li><a tabindex="-1" href="#" id="btnMenuDrivers">Drivers</a></li>
							<li><a tabindex="-1" href="#" id="btnMenuVehicleAssignements">Vechicles assignment</a></li>
						</ul>
					</li>
				</ul>

				<ul class="nav pull-right">
					@{/***** MESSAGES *****/}
					<li class="dropdown">
						<a href="#" role="button"><span class="badge badge-inverse">123 <i class="icon-large icon-envelope icon-white"></i></span></a>
					</li>

					@{/***** BADGE EVENTS *****/}
					<li class="dropdown">
						<a href="#" id="drop_events" role="button" class="dropdown-toggle" data-toggle="dropdown"><span class="badge badge-important">123</span> Events<b class="caret"></b></a>

						<ul class="dropdown-menu" role="menu" aria-labelledby="drop_events">
							<li><a tabindex="-1" href="#"><span class="badge badge-important">3</span> Important</a></li>
							<li><a tabindex="-1" href="#"><span class="badge badge-warning">117</span> Warning</a></li>
							<li><a tabindex="-1" href="#"><span class="badge badge-info">76</span> Informative</a></li>
							<li class="divider"></li>
							<li><a tabindex="-1" href="#"><i class="icon-large icon-trash"></i> Reset all counters</a></li>
						</ul>
					</li>

					@{/***** SYSTEM MANAGER MENU *****/}
					<li class="dropdown">
						<a href="#" id="drop_sys_menu" role="button" class="dropdown-toggle" data-toggle="dropdown"><i class="icon-large icon-cog icon-white"></i> Manage<b class="caret"></b></a>

						<ul class="dropdown-menu" role="menu" aria-labelledby="drop_sys_menu">
							<li><a tabindex="-1" href="#" class="workinprogress" id="top-menu-organizations"><i class="icon-large icon-th"></i> Organizations</a></li>
							<li><a tabindex="-1" href="#" class="workinprogress" id ="top-menu-security"><i class="icon-large icon-lock"></i> Security</a></li>
							<li><a tabindex="-1" href="#" class="workinprogress" id ="top-menu-system"><i class="icon-large icon-wrench"></i> System</a></li>
						</ul>
					</li>

					@{/***** USER MENU *****/}
					<li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">
							<i class="icon-user icon-white"></i> @user.displayName
							<b class="caret"></b>
						</a>
						<ul class="dropdown-menu">

							<li>
								<div style="width: 324px">
									<div class="modal-header">
										<div class="row-fluid">
											<div class="span3">
												<img src="@routes.Assets.at("img/defaultPhoto.png")" alt="" class="thumbnail span12">
											</div>
											<div class="span9">
												<h4>@user.displayName</h4>
												<h5>Administrator</h5>
											</div>
										</div>
									</div>
									<div class="modal-body">
										<div class="row-fluid">
											<div class="span12">
												
													<a href="#" class="workinprogress"><i class="icon-user"></i> Account settings</a>
													<a href="#" class="workinprogress"><i class="icon-th-list"></i> Preferences</a>
													<a href="#" class="workinprogress"><i class="icon-cog"></i> Special task #1</a>
													<a href="#" class="workinprogress"><i class="icon-cog"></i> Special task #2</a>
												
											</div>
										</div>

									</div>
									<div class="modal-footer">
										<button href="#" class="btn">Require assistance</button>
										<button href="#" class="btn btn-primary" id="btnLogout"><i class="icon-off icon-white"></i> Log Out</button>
									
									</div>
								</div>

							</li>

						</ul>
					</li>
				</ul>
			</div>
		</div>
	</div>
</div>
<br/>
<button class="btn createmenu" data-device-id="x">create</button>
<br/>
<div class="navbar">
	<div class="navbar navbar-static">
		<div class="navbar-inner">
			<div class="container" style="width: auto;" id="containerProva">
				<a class="brand" href="#">Nox</a>
			</div>
		</div>
	</div>
</div>
<style type='text/css'>
	.workinprogress { color:#DDDDDD !important; }
</style>
<script type='text/javascript'>
	steal('jquery/jquery.js')
	.then('/assets/webapp/menu/menu.js')
	.then(
		function() {
			jQuery(function($){
				
				$(".workinprogress").click(function(el, ev) {
					alert("Work in progress...");	
				});
				
				$("#btnLogout").click(function(el, ev) {
					window.location.pathname = jsRoutes.controllers.Application.logout().url;
				});
				
				$("#btnDevices").click(function(el, ev) {
					window.location.pathname = jsRoutes.controllers.Device.index().url;
				});
				
				$("#btnDeviceTypes").click(function(el, ev) {
					window.location.pathname = jsRoutes.controllers.DeviceType.index().url;
				});
				
				$("#btnDeviceGroups").click(function(el, ev) {
					window.location.pathname = jsRoutes.controllers.DeviceGroup.index().url;
				});
				
				$("#btnMenuVehicle").click(function(el, ev) {
					window.location.pathname = jsRoutes.controllers.Vehicle.index().url;
				});
				
				$("#btnMenuDrivers").click(function(el, ev) {
					window.location.pathname = jsRoutes.controllers.Driver.index().url;
				});
				
				$("#btnMenuVehicleAssignements").click(function(el, ev) {
					window.location.pathname = jsRoutes.controllers.VehicleAssignement.index().url;
				});
				
				$("#btnSimcards").click(function(el, ev) {
					window.location.pathname = jsRoutes.controllers.Simcard.index().url;
				});
				
				$(".btn.createmenu").click(function(el, ev) {
					$("#containerProva").webapp_menu(
						{
							"menu" :	[
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
					);
				});
				
			});
		});
</script>