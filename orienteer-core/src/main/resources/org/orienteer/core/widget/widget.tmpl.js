var gridster;
var width = $("#${componentId}").width();
var margin = 5;
function gridsterChanged()
{
	var serialized = gridster.serialize();
	${callBackScript}
}
gridster = $('#${componentId}> ul')
					.gridster({
						widget_margins: [margin, margin],
						widget_base_dimensions: [width/2-2*margin, 300],
						min_cols: 2,
						serialize_params : function($w, wgd) { 
											return { 
												id: $w.attr('id'),
												col: wgd.col, 
												row: wgd.row, 
												size_x: wgd.size_x, 
												size_y: wgd.size_y 
												} 
											},
						draggable: {
				            handle: 'div.panel-heading',
				            stop: gridsterChanged
						},
						resize: {
				            enabled: true,
				            stop: gridsterChanged
				        }
					}).data('gridster');