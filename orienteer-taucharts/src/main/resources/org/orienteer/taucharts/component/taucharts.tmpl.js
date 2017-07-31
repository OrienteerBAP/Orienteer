!function() {
	function renderData(data) {
		for (var row in data) {
			for (var col in data[row]) {
				if (col.match('@')){
					delete data[row][col];
				}
			}
		}
		var config = {
	        guide: {
	            x: {label:"${xLabel}"},  
	            y: {label:"${yLabel}"},   
	          },
			data: data,
		    type: "${type}",
		    x: ${x},
		    y: ${y},
		    color: "${colorBy}", 
		    plugins:${plugins}   
		};
		var addConfig = eval("${config}");
		
		if(Object.assign && (typeof addConfig == "object")) config = Object.assign(config, addConfig);
		
		var chart = new tauCharts.Chart(config);
		chart.renderTo("#"+'${componentId}');
	}
	
	var rest = ${rest};
	if(rest) {
		var restUrl = "${url}";
		$.getJSON( '${url}', function( data ) {
				renderData(data.result);
			});
	} else {
		renderData(eval("${data}"));
	}
}();