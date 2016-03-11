!function() {
	var url = '${dataUrl}';
	var config = JSON.parse('${config}');
	config.hiddenAttributes = ['@type', '@rid', '@version'];
	var renderers = $.extend($.pivotUtilities.renderers, $.pivotUtilities.d3_renderers, 
			$.pivotUtilities.c3_renderers);
	config.renderers = renderers;
	config.onRefresh = function(originalConfig) {
		var cfg = JSON.parse(JSON.stringify(originalConfig));
        //delete some values which are functions
        delete cfg["aggregators"];
        delete cfg["renderers"];
        delete cfg["onRefresh"];
        //delete some bulky default values
        delete cfg["rendererOptions"];
        delete cfg["localeStrings"];
        ${callBackScript}
	};
	var editMode = ${editMode};
	
	
	$.getJSON( url, function( data ) {
		if(editMode) {
			$("#${componentId}").pivotUI(data.result, config);
		} else {
			$("#${componentId}").pivot(data.result, config);
		}
	});
}();