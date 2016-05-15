!function() {
	var url = '${dataUrl}';
	var config = JSON.parse('${config}');
	var language = '${language}';
	config.hiddenAttributes = ['@type', '@rid', '@version'];
	var renderers = $.extend($.pivotUtilities.renderers, $.pivotUtilities.d3_renderers, 
			$.pivotUtilities.c3_renderers);
	var translate = function(name, srcLng, dstLng, src, dst) {
		if(srcLng==dstLng) return name;
		var indx = Object.keys(src).indexOf(name);
		return indx>=0?Object.keys(dst)[indx]:name;
	}
	config.renderers = renderers;
	config.onRefresh = function(originalConfig) {
		var cfg = JSON.parse(JSON.stringify(originalConfig));
		//Translate aggregatorName and rendererName to en
		cfg.aggregatorName = translate(cfg.aggregatorName, language, 'en', originalConfig.aggregators, $.pivotUtilities.aggregators);
		cfg.rendererName = translate(cfg.rendererName, language, 'en', originalConfig.renderers, $.pivotUtilities.renderers);
		
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
			config.aggregatorName = translate(config.aggregatorName, 'en', language, $.pivotUtilities.aggregators, $.pivotUtilities.locales[language].aggregators);
			//Do not need to translate renderers: they are on english 
			//config.rendererName = translate(config.rendererName, 'en', language, $.pivotUtilities.renderers, $.pivotUtilities.locales[language].renderers);
			$("#${componentId}").pivotUI(data.result, config, false, language);
		} else {
			if(config.rendererName) config.renderer = config.renderers[config.rendererName];
			if(config.aggregatorName) config.aggregator = $.pivotUtilities.aggregators[config.aggregatorName](config.vals);
			if('en'!=language) {
				config.localeStrings = $.pivotUtilities.locales[language].localeStrings;
				config.aggregatorName = translate(config.aggregatorName, 'en', language, $.pivotUtilities.aggregators, $.pivotUtilities.locales[language].aggregators);
			}
			$("#${componentId}").pivot(data.result, config);
		}
	});
}();