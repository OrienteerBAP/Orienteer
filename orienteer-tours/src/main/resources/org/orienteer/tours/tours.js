function OTours() {
	return this;
}

OTours.prototype = {
	plugin : null,
	start : function() {
		$.getJSON("/otours/tours", function(data){
			if(data && data.length>0) {
				this.plugin = new TourPlugin();
				this.plugin.startTour(data[0]);
			}
		});
	}
}

window.otour = new OTours();