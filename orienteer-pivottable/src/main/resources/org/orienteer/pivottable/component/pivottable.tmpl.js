!function() {
	var url = '${dataUrl}';
	var config = JSON.parse('${config}');
	config.hiddenAttributes = ['@type', '@rid', '@version'];
	var editMode = ${editMode};
	
	$.get( url, function( data ) {
		if(editMode) {
			$("#${componentId}").pivotUI(data.result, config);
		} else {
			$("#${componentId}").pivot(data.result, config);
		}
	});
}();