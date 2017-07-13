!function() {
	$.getJSON( '${url}', function( data ) {
		for (var row in data.result) {
			for (var col in data.result[row]) {
				if (col.match('@')){
					delete data.result[row][col];
				}
			}
		}
		var chart = new tauCharts.Chart({
	        guide: {
	            x: {label:'${xLabel}'},  
	            y: {label:'${yLabel}'},   
	          },
			data: data.result,
		    type: '${type}',
		    x: '${x}',
		    y: '${y}',
		    color: '${colorBy}', 
		    plugins:${plugins}   
		});
		chart.renderTo("#"+'${componentId}');
	});
}();