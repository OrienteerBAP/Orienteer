!function() {
	var url = '${dataUrl}';
	var config = JSON.parse('${config}');
	config.hiddenAttributes = ['@type', '@rid', '@version'];
	console.log(url);
	console.log(config);
	
	$.get( url, function( data ) {
		$("#${componentId}").pivotUI(data.result, config);
	});
}();