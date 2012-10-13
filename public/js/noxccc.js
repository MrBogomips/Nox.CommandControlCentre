jQuery(document).ready(function($){
	$("#top-menu-organizations").tooltip({
		html: true,
		placement: 'left',
		trigger: 'hover',
		title: 'Manage organizations and users<br><input type="checkbox"> dont\'t show again',
		delay: {show: 500, hide: 100}
	});
	$("#top-menu-security").tooltip({
		html: true,
		placement: 'left',
		trigger: 'hover',
		title: 'Manage features\' ACL<br><input type="checkbox"> dont\'t show again',
		delay: {show: 500, hide: 100}
	});
	$("#top-menu-system").tooltip({
		html: true,
		placement: 'left',
		trigger: 'hover',
		title: 'Manage backups, services and system-wide config<br><input type="checkbox"> dont\'t show again',
		delay: {show: 500, hide: 100}
	});
});