function TourPlugin() {
	return this;
}

TourPlugin.prototype = {
	thisTour : null,
	startTour : function(config) {
		console.log(config);
		config.framework = 'bootstrap4';
		config.debug = true;
		config.backdrop = true;
		/*config.backdropOptions =    {
		    highlightOpacity: 0.1,
		    highlightColor: '#F00'
		};*/
		
		this.thisTour = new Tour(config);
		this.thisTour.restart();
	}
}