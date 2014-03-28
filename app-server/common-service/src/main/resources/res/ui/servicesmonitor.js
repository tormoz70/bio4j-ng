var askServerData = {"action" : "", "data": {}};

function askServer(handler, dataToSend, parseData) {
	$.get(handler, dataToSend, function(serverData) {
		parseData(serverData);
	}, "json");	
}

$(document).ready(function() {
	var dlg = $('#waitDlg').dialog({
		modal    : true,
		autoOpen : false,
		draggable: false,
		resizable: false,
		closeOnEscape: false
	});

	$('#services-tabs').tabs({ajaxOptions: {
		beforeSend : function() { dlg.dialog('open') },
		complete   : function() { dlg.dialog('close')}
	}}).tabs('paging');
});