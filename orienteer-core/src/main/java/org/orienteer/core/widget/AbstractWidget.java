package org.orienteer.core.widget;

import com.google.common.base.MoreObjects;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.ICommandsSupportComponent;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.event.ActionPerformedEvent;
import org.orienteer.core.method.MethodPlace;
import org.orienteer.core.method.MethodsView;
import org.orienteer.core.util.LocalizeFunction;
import org.orienteer.core.web.ODocumentPage;
import org.orienteer.core.widget.command.FullScreenCommand;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.FunctionModel;
import ru.ydn.wicket.wicketorientdb.model.NvlModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;

import static org.orienteer.core.module.OWidgetsModule.OPROPERTY_HIDDEN;

/**
 * Abstract root class for widgets
 *
 * @param <T> the type of main data object linked to this widget
 */
public abstract class AbstractWidget<T> extends GenericPanel<T> implements ICommandsSupportComponent<T> {
	
	private boolean hidden=false;
	
	private int loadedWidgetVersion=-1;
	
	protected RepeatingView commands;
	
	private IModel<ODocument> widgetDocumentModel;

	private MethodsView methods;
	
	public AbstractWidget(String id, IModel<T> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model);
		this.widgetDocumentModel = widgetDocumentModel;
		setOutputMarkupId(true);
//		setOutputMarkupPlaceholderTag(true);
		add(commands = new RepeatingView("commands"));
		methods = new MethodsView(commands, model,MethodPlace.ACTIONS,null);
		methods.overrideBootstrapType(null);
		addCommand(new AjaxCommand<T>(commands.newChildId(), "command.settings") {
			
			@Override
			public void onClick(AjaxRequestTarget target) {
				ODocument doc = getWidgetDocument();
				if(doc.getIdentity().isPersistent()) {
					setResponsePage(new ODocumentPage(doc));
				}
				else {
					String alert = "alert('"+JavaScriptUtils.escapeQuotes(getLocalizer().getString("warning.widget.nosettings", AbstractWidget.this))+"')";
					target.appendJavaScript(alert);
				}
			}
		});
		addCommand(new AjaxCommand<T>(commands.newChildId(), "command.hide") {
			
			@Override
			public void onClick(AjaxRequestTarget target) {
				DashboardPanel<T> dashboard = getDashboardPanel();
				dashboard.getDashboardSupport().ajaxDeleteWidget(AbstractWidget.this, target);
				setHidden(true);
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(getDashboardPanel().getModeObject().canModify());
			}
		});
		addCommand(new AjaxCommand<T>(commands.newChildId(), "command.delete") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				DashboardPanel<T> dashboard = getDashboardPanel();
				dashboard.getDashboardSupport().ajaxDeleteWidget(AbstractWidget.this, target);
				dashboard.deleteWidget(AbstractWidget.this);
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(getDashboardPanel().getModeObject().canModify());
			}
		});
		addCommand(new FullScreenCommand<T>(commands.newChildId()));
	}

	@Override
	public AbstractWidget<T> addCommand(Command<T> command) {
		command.setBootstrapType(null);
		commands.add(command);
		return this;
	}
	
	@Override
	public AbstractWidget<T> removeCommand(Command<T> command) {
		commands.remove(command);
		return this;
	}


	@Override
	public String newCommandId() {
		return commands.newChildId();
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
	
	protected final IModel<String> getTitleModel() {
		return new NvlModel<String>(new FunctionModel<Object, String>(
												new ODocumentPropertyModel<Object>(getWidgetDocumentModel(), "title"), 
												LocalizeFunction.getInstance()), 
											getDefaultTitleModel());
	}
	
	protected abstract FAIcon newIcon(String id);
	
	protected abstract IModel<String> getDefaultTitleModel();
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisibilityAllowed(!hidden);
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
		hidden = MoreObjects.firstNonNull((Boolean)doc.field(OPROPERTY_HIDDEN), false);
		getDashboardPanel().getDashboardSupport().loadSettings(this, doc);
		loadedWidgetVersion = doc.getVersion();
	}
	
	public void saveSettings() {
		ODocument doc = widgetDocumentModel.getObject();
		if(doc==null) return;
		getDashboardPanel().getDashboardSupport().saveSettings(this, doc);
		doc.field(OPROPERTY_HIDDEN, hidden);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(newIcon("icon"));
		add(new Label("title", getTitleModel()));
		getDashboardPanel().getDashboardSupport().initWidget(this);
		loadSettings();
		methods.loadMethods();
	}
	
	@Override
	protected void onBeforeRender() {
		// Reload settings of widget if they were changed
		ODocument doc = getWidgetDocument();
		if(doc!=null && doc.getVersion()!=loadedWidgetVersion) {
			loadSettings();
		}
		super.onBeforeRender();
	}
	
	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		String widgetStyleClass = getWidgetStyleClass();
		if(widgetStyleClass!=null) {
			tag.append("class", widgetStyleClass, " ");
		}
	}
	
	protected String getWidgetStyleClass() {
		return null;
	}
	
	@Override
	public void onEvent(IEvent<?> event) {
		Object payload = event.getPayload();
		if(payload instanceof ActionPerformedEvent) {
			onActionPerformed((ActionPerformedEvent<?>)payload, event);
		}
	}
	
	public void onActionPerformed(ActionPerformedEvent<?> event, IEvent<?> wicketEvent) {
		//for soft overriding
	}
	
	protected ODatabaseDocument getDatabase() {
		return OrientDbWebSession.get().getDatabase();
	}
	
	protected OSchema getSchema() {
		return OrientDbWebSession.get().getSchema();
	}
}
