package org.orienteer.core.widget;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.FAIcon;

/**
 * Abstract root class for widgets
 *
 * @param <T> the type of main data object linked to this widget
 */
public abstract class AbstractWidget<T> extends GenericPanel<T> {
	
	private Integer col;
	private Integer row;
	private int sizeX=1;
	private int sizeY=1;

	public AbstractWidget(String id, IModel<T> model) {
		super(id, model);
		add(newIcon("icon"));
		add(new Label("title", getTitleModel()));
		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);
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

	public Integer getCol() {
		return col;
	}

	public void setCol(Integer col) {
		this.col = col;
	}

	public Integer getRow() {
		return row;
	}

	public void setRow(Integer row) {
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

}
