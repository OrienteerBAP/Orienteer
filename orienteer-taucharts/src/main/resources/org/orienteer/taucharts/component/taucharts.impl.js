!function() {
	var jsonData = ${data};
	for (var row in jsonData) {
		for (var col in jsonData[row]) {
			if (col.match('@')){
				delete jsonData[row][col];
			}
		}
	}
	var chart = new tauCharts.Chart({
        guide: {
            x: {label:'${xLabel}'},  
            y: {label:'${yLabel}'},   
          },
		data: jsonData,
	    type: '${type}',
	    x: '${x}',
	    y: '${y}',
	    color: '${colorBy}', 
	    plugins:${plugins}   
	});
	chart.renderTo("#"+'${componentId}');
}();