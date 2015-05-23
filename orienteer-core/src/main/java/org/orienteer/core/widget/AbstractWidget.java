package org.orienteer.core.widget;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.command.AjaxCommand;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import static org.orienteer.core.module.OWidgetsModule.*;

import com.google.common.base.Objects;
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
	private boolean hidden=false;
	
	private RepeatingView commands;
	
	private IModel<ODocument> widgetDocumentModel;
	
	public AbstractWidget(String id, IModel<T> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model);
		this.widgetDocumentModel = widgetDocumentModel;
		add(newIcon("icon"));
		add(new Label("title", getTitleModel()));
		setOutputMarkupId(true);
//		setOutputMarkupPlaceholderTag(true);
		commands = new RepeatingView("commands");
		commands.add(new AjaxCommand<T>(commands.newChildId(), "command.delete") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				DashboardPanel<T> dashboard = getDashboardPanel();
				dashboard.getDashboardSupport().ajaxDeleteWidget(AbstractWidget.this, target);
				dashboard.deleteWidget(AbstractWidget.this);
			}
		});
		commands.add(new AjaxCommand<T>(commands.newChildId(), "command.hide") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				DashboardPanel<T> dashboard = getDashboardPanel();
				dashboard.getDashboardSupport().ajaxDeleteWidget(AbstractWidget.this, target);
				setHidden(true);
			}
		});
		add(commands);
		loadSettings();
	}
	
	public DashboardPanel<T> getDashboardPanel() {
		DashboardPanel<T> dashboard = findParent(DashboardPanel.class);
		if(dashboard==null)
		{
			throw new WicketRuntimeException("No dashboard found for widget: "+this);
		}
		return dashboard;
	}
	
	public IModel<ODocument> getWidgetDocumentModel() {
		return widgetDocumentModel;
	}
	
	public ODocument getWidgetDocument() {
		return widgetDocumentModel.getObject();
	}
	
	protected abstract FAIcon newIcon(String id);
	
	protected abstract IModel<String> getTitleModel();
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisibilityAllowed(!hidden);
	}
	
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
	
	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public void loadSettings() {
		ODocument doc = widgetDocumentModel.getObject();
		if(doc==null) return;
		
		row = Objects.firstNonNull((Integer)doc.field(OPROPERTY_ROW), 1);
		col = Objects.firstNonNull((Integer)doc.field(OPROPERTY_COL), 1);
		sizeX = Objects.firstNonNull((Integer)doc.field(OPROPERTY_SIZE_X), 2);
		sizeY = Objects.firstNonNull((Integer)doc.field(OPROPERTY_SIZE_Y), 1);
		hidden = Objects.firstNonNull((Boolean)doc.field(OPROPERTY_HIDDEN), false);
	}
	
	public void saveSettings() {
		ODocument doc = widgetDocumentModel.getObject();
		if(doc==null) return;
		doc.field(OPROPERTY_ROW, row);
		doc.field(OPROPERTY_COL, col);
		doc.field(OPROPERTY_SIZE_X, sizeX);
		doc.field(OPROPERTY_SIZE_Y, sizeY);
		doc.field(OPROPERTY_HIDDEN, hidden);
		doc.save();
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		getDashboardPanel().getDashboardSupport().initWidget(this);
	}
}
