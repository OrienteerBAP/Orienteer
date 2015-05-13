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
 * @param <S> the type of settings for this widget
 */
public abstract class AbstractWidget<T, S extends IWidgetSettings> extends GenericPanel<T> {
	
	protected S settings;
	
	public AbstractWidget(String id, S settings, IModel<T> model) {
		super(id, model);
		this.settings = settings;
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
		tag.put("data-row", getSettings().getRow());
		tag.put("data-col", getSettings().getCol());
		tag.put("data-sizex", getSettings().getSizeX());
		tag.put("data-sizey", getSettings().getSizeY());
	}
	
	public S getSettings() {
		return settings;
	}

}
