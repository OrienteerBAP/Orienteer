'use strict';

var timeoutID;
var needShow = false;

function showAjaxIndicator() {
	needShow = true;
    timeoutID = window.setTimeout(function () {
        if (needShow) $('#${componentId}').show();
    }, 300);
}

function hideAjaxIndicator() {
	needShow = false;
    window.clearTimeout(timeoutID);
    $('#${componentId}').hide();
}

Wicket.Event.subscribe('/ajax/call/beforeSend', 
	function( attributes, jqXHR, settings ) {
		showAjaxIndicator();
    });

Wicket.Event.subscribe('/ajax/call/complete', 
	function( attributes, jqXHR, textStatus) {
		hideAjaxIndicator();
    });
