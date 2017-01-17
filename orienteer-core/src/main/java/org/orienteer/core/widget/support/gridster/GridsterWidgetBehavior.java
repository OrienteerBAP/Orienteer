package org.orienteer.core.widget.support.gridster;

import static org.orienteer.core.module.OWidgetsModule.OPROPERTY_COL;
import static org.orienteer.core.module.OWidgetsModule.OPROPERTY_ROW;
import static org.orienteer.core.module.OWidgetsModule.OPROPERTY_SIZE_X;
import static org.orienteer.core.module.OWidgetsModule.OPROPERTY_SIZE_Y;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.util.lang.Args;
import org.orienteer.core.widget.AbstractWidget;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.orientechnologies.orient.core.record.impl.ODocument;

class GridsterWidgetBehavior extends Behavior {

	private AbstractWidget<?> component;
	
	private int col=1;
	private int row=1;
	private int sizeX=1;
	private int sizeY=1;
	
	@Override
	public final void bind(final Component hostComponent)
	{
		Args.notNull(hostComponent, "hostComponent");

		if (component != null)
		{
			throw new IllegalStateException("this kind of handler cannot be attached to " +
				"multiple components; it is already attached to component " + component +
				", but component " + hostComponent + " wants to be attached too");
		}
		if(!(hostComponent instanceof AbstractWidget))
		{
			throw new IllegalStateException("This behaviour can be attached only to "+AbstractWidget.class.getSimpleName()+
											", but this one: "+hostComponent.getClass().getName());
		}

		component = (AbstractWidget<?>)hostComponent;
	}
	
	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
		AbstractWidget<?> widget = (AbstractWidget<?>) component;
		if(widget.getWebRequest().isAjax())
		{
			String script = "$('#"+widget.getDashboardPanel().getMarkupId()+" > ul').data('gridster').ajaxUpdate('"+widget.getMarkupId()+"');";
			response.render(OnDomReadyHeaderItem.forScript(script));
		}
	}
	
	@Override
	public void onComponentTag(Component component, ComponentTag tag) {
		super.onComponentTag(component, tag);
		tag.put("data-row", getRow());
		tag.put("data-col", getCol());
		tag.put("data-sizex", getSizeX());
		tag.put("data-sizey", getSizeY());
	}
	
	public static GridsterWidgetBehavior getBehaviour(AbstractWidget<?> widget)
	{
		return widget.getBehaviors(GridsterWidgetBehavior.class).get(0);
	}
	
	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getSizeX() {
		return sizeX;
	}

	public void setSizeX(int sizeX) {
		this.sizeX = sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public void setSizeY(int sizeY) {
		this.sizeY = sizeY;
	}
	
	public void saveSettings(ODocument doc) {
		doc.field(OPROPERTY_ROW, row);
		doc.field(OPROPERTY_COL, col);
		doc.field(OPROPERTY_SIZE_X, sizeX);
		doc.field(OPROPERTY_SIZE_Y, sizeY);
	}
	
	public void loadSettings(ODocument doc) {
		row = MoreObjects.firstNonNull((Integer)doc.field(OPROPERTY_ROW), 1);
		col = MoreObjects.firstNonNull((Integer)doc.field(OPROPERTY_COL), 1);
		sizeX = MoreObjects.firstNonNull((Integer)doc.field(OPROPERTY_SIZE_X), 2);
		sizeY = MoreObjects.firstNonNull((Integer)doc.field(OPROPERTY_SIZE_Y), 1);
	}
}
