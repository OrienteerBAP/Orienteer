!function() {
	var url = '${dataUrl}';
	var config = JSON.parse('${config}');
	config.hiddenAttributes = ['@type', '@rid', '@version'];
	var renderers = $.extend($.pivotUtilities.renderers, $.pivotUtilities.d3_renderers, 
			$.pivotUtilities.c3_renderers);
	config.renderers = renderers;
	var editMode = ${editMode};
	
	
	$.getJSON( url, function( data ) {
		if(editMode) {
			$("#${componentId}").pivotUI(data.result, config);
		} else {
			$("#${componentId}").pivot(data.result, config);
		}
	});
}();