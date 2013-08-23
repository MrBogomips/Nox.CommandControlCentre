@(groups: Seq[DeviceGroupPersisted], user: UserTrait)

@header_more = {}

@footer_scripts = {
<script type='text/javascript'>
	steal('/assets/webapp/webapp_init.js')
	.then('/assets/webapp/devicegroups/devicegroups.js')
	.then('/assets/webapp/pager/pager.js')
	.then(function() {
		$("#create_device").click(function() {
			var $el = $("<div></div>")
			$('body').append($el);
			$el.webapp_devicegroups();
		});

		$(".btn.modify").click(function(el, ev) {
			var options = {};
			options["id"] = $(this).attr("data-device-id");
			var $el = $("<div></div>");
			$('body').append($el);
			$el.webapp_devicegroups(options);
		});
		
		$(".btn.delete").click(function(el, ev) {
			var id = $(this).attr("data-device-id");
			jsRoutes.controllers.DeviceGroup.delete(id).ajax()
			.done(function(data, txtStatus, jqXHR) {
				location.reload(true);
			})
			.fail(function(data, txtStatus, jqXHR) {
				var $alert= $("<div class='alert alert-block alert-error'><button type='button' class='close' data-dismiss='alert'>x</button><h4 class='alert-heading'>An error occurred</h4><p>"+data.responseText+"</p></div>");
				self.find(".alert_placeholder").html($alert);
			});
		});
		
		$(".btn.pager").click(function(el, ev) {
			$('#aProva').webapp_pager({ 
				'datasource' : { 'uri' : jsRoutes.controllers.DeviceGroup } ,
				'cssclass' : 'ana-devices groups' ,
				'header': { 'title' : 'Device groups' , 'render': null } ,
				'footer': { 'render': function() { return '<button id="create_device" class="btn btn-primary">Create new device group</button>' } } ,
				'paging': { 'show': false , 'page_size' : 10 , 'page_index_init' : 0 } ,
				'searching': { 'show': false } ,
				'coloumns' : [ { 'title' : 'Groups ID' , 'map' : 'id' } , 
				              { 'title' : 'Name' , 'map' : 'name' } ,
				              { 'title' : 'Display Name' , 'map' : 'displayName' } ,
				              { 'title' : 'Description' , 'map' : 'description' } ,
				              { 'title' : 'Enabled' , 'map' : 'enabled' , 'renderAs' : 'switch' } ,
				              { 'title' : '' , 'renderAs' : 'html' , 'renderHtml' : function(id) { return '<nobr><button data-device-id="' + id + '" class="btn modify">Modify</button><button data-device-id="' + id + '" class="btn btn-danger delete">Delete</button></nobr>' } } ]
			});
			
		});
	});
</script>
}

@aria.master.authenticated("Device groups", user, header_more = header_more, footer_more = footer_scripts) {

<div class="ana-devices groups">
	<h1>Device groups</h1>
	
	<div class="alert_placeholder"></div>

	<table class="table table-condensed table-bordered table-striped">
		<thead>
			<tr>
				<th>Groups ID</th>
				<th>Name</th>
				<th>Display Name</th>
				<th>Description</th>
				<th>Enabled</th>
				<th width="1%"></th>
			</tr>
		</thead>
		<tbody>
		@groups.map { g =>
			<tr>
				<td>@g.id</td>
				<td>@g.name</td>
				<td>@g.displayName</td>
				<td>@g.description</td>
				<td><div class="switch">
					<input type="checkbox" @if(g.enabled){
						checked
					} disabled >
					</div>
					</td>
				<td>
				<nobr>
					<button class="btn modify" data-device-id="@g.id">Modify</button>
					<button class="btn btn-danger delete" data-device-id="@g.id">Delete</button>
				</nobr>
				</td>
			</tr>
		}
		</tbody>
	</table>
	<button class="btn btn-primary" id="create_device">Create new device group</button>
	</div>

<button class="btn btn-danger pager" data-device-id="btnPager">Pager</button>
<div id="aProva">
	
</div>
}