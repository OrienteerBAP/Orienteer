function TourPlugin() {
	return this;
}

TourPlugin.prototype = {
	thisTour : null,
	startTour : function(config) {
		console.log(config);
		var steps = config.steps.map(function(s){
			return {
				element: s.element,
				popover: {
					title: s.title,
					description: s.content
				}
			}
		});
		console.log(steps);
		this.thisTour = new Driver({
			animate: false
		});
		this.thisTour.defineSteps(steps);
		this.thisTour.start();
	}
}