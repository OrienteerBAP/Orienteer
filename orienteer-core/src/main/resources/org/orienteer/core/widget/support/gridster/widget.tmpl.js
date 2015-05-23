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
gridster.gridsterChanged = gridsterChanged;

gridster.ajaxUpdate = function(id)
{
	var w = $('#'+id);
	var gr = gridster;
	var i;
	for(i=0; i< gr.$widgets.length; i++) 
	{
		if(gr.$widgets[i].id==id) break;	
	}
	var o = $(gr.$widgets[i]);
	gr.remove_from_gridmap(gr.dom_to_coords(o));
	/*var o = $(gr.$widgets[i]);
	w.attr('data-col', o.attr('data-col'));
	w.attr('data-row', o.attr('data-row'));
	w.attr('data-sizex', o.attr('data-sizex'));
	w.attr('data-sizey', o.attr('data-sizey'));*/
	gr.$widgets[i] = w[0];
	
	var wgd = gr.dom_to_coords(w);
	var empty_upper_row = this.can_go_widget_up(wgd);
    if (empty_upper_row) {
        wgd.row = empty_upper_row;
        w.attr('data-row', empty_upper_row);
    }
    w.data('coords', w.coords());
    w.data('coords').grid = wgd;
    gr.update_widget_position(wgd, w);
    w.addClass('gs-w');
    gr.options.resize.enabled && gr.add_resize_handle(w);
}