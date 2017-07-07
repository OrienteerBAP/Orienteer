!function() {
	var plugins = ${plugins};
	
	var chart = new tauCharts.Chart({
        guide: {
            x: {label:'${xLabel}'},  
            y: {label:'${yLabel}'},   
          },
		data: ${data},
	    type: '${type}',
	    x: '${x}',
	    y: '${y}',
	    color: '${colorBy}', 
	    plugins:${plugins}   
	});
	chart.renderTo("#"+'${componentId}');
}();