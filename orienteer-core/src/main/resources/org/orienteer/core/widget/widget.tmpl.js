var width = $("#${componentId}").width();
var margin = 5;
var gridster = $('#${componentId}> ul')
					.gridster({
						widget_margins: [margin, margin],
						widget_base_dimensions: [width/2-2*margin, 300],
						draggable: {
				            handle: 'div.panel-heading'
						},
						resize: {
				            enabled: true
				        }
					});