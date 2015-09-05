function showAjaxIndicator() {
	$('#${componentId}').show();
}

function hideAjaxIndicator() {
	$('#${componentId}').hide();
}
Wicket.Event.subscribe('/ajax/call/beforeSend', 
	function( attributes, jqXHR, settings ) {
		showAjaxIndicator()
    });
Wicket.Event.subscribe('/ajax/call/complete', 
	function( attributes, jqXHR, textStatus) {
		hideAjaxIndicator()
    });
