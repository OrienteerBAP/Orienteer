package org.orienteer.core.widget;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.FAIcon;

import static org.orienteer.core.module.OWidgetsModule.*;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Abstract root class for widgets
 *
 * @param <T> the type of main data object linked to this widget
 */
public abstract class AbstractWidget<T> extends GenericPanel<T> {
	
	private int col=1;
	private int row=1;
	private int sizeX=1;
	private int sizeY=1;
	
	public AbstractWidget(String id, IModel<T> model) {
		super(id, model);
		add(newIcon("icon"));
		add(new Label("title", getTitleModel()));
		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);
	}
	
	public DashboardPanel<T> getDashboardPanel() {
		DashboardPanel<T> dashboard = findParent(DashboardPanel.class);
		if(dashboard==null)
		{
			throw new WicketRuntimeException("No dashboard found for widget: "+this);
		}
		return dashboard;
	}
	
	protected abstract FAIcon newIcon(String id);
	
	protected abstract IModel<String> getTitleModel();
	
	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		tag.put("data-row", getRow());
		tag.put("data-col", getCol());
		tag.put("data-sizex", getSizeX());
		tag.put("data-sizey", getSizeY());
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
	
	public void loadSettings(ODocument doc) {
		if(doc==null) return;
		row = doc.field(OPROPERTY_ROW);
		col = doc.field(OPROPERTY_COL);
		sizeX = doc.field(OPROPERTY_SIZE_X);
		sizeY = doc.field(OPROPERTY_SIZE_Y);
	}
	
	public void saveSettings(ODocument doc) {
		if(doc==null) return;
		doc.field(OPROPERTY_ROW, row);
		doc.field(OPROPERTY_COL, col);
		doc.field(OPROPERTY_SIZE_X, sizeX);
		doc.field(OPROPERTY_SIZE_Y, sizeY);
	}
}
