function loadStatistics() {
	askServerData.action = "load";
	askServer(pluginRoot + "/reload.json?pluginId=" + pluginLabel, askServerData, updateData);
}

function updateData(serverResult) {
	$('#connection').empty();
	$('#serverProperties').empty();
	$('#queues').empty();
	var rabbitConnection = serverResult.connection;
	var queueList = serverResult.queues;
	
	var connText = "<li><b>Address</b>: " + rabbitConnection.hostname + " (" + rabbitConnection.address + ")</li>";
	connText += "<li><b>Port</b>: " + rabbitConnection.port + "</li>";
	connText += "<li><b>Status</b>: " + (rabbitConnection.status ? "Active" : "InActive") + "</li>";
	var srvProps = rabbitConnection.serverProperties;
	var srvPropsText = "";
	for (var i = 0; i < srvProps.length; i++) {
		var srvProp = srvProps[i];
		srvPropsText += "<li><b>" + srvProp.name + "</b>: ";
		if (typeof(srvProp.value) == "object") {
			var counter = 0;
			for (var key in srvProp.value) {
				if (counter > 0)
					srvPropsText += "; ";
				srvPropsText += key + "=" + hasOwnProperty.call(key, srvProp.value);
				counter++;
			}
		} else {
			srvPropsText += srvProp.value;
		}
		srvPropsText += "</li>";
	}
	$('#connection').append(connText);
	$('#serverProperties').append(srvPropsText);
	
	var queueText = "";
	for (var i = 0; i < queueList.length; i++) {
		var oQueue = queueList[i]; 
		queueText += "<li>Name: " + oQueue.queue + " Channels: " + oQueue.connections +  "</li>";
	}
	$('#queues').append(queueText);
}

$(document).ready(function() {
	loadStatistics();
});
