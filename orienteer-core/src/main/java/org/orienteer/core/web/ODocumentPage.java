package org.orienteer.core.web;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.CustomAttributes;
import org.orienteer.core.MountPath;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.ODocumentPageHeader;
import org.orienteer.core.component.SchemaPageHeader;
import org.orienteer.core.component.TabsPanel;
import org.orienteer.core.component.command.EditCommand;
import org.orienteer.core.component.command.EditODocumentCommand;
import org.orienteer.core.component.command.SaveODocumentCommand;
import org.orienteer.core.component.meta.ODocumentMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.property.OClassViewPanel;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.model.ODocumentNameModel;
import org.orienteer.core.service.IOClassIntrospector;

import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;

import com.google.inject.Inject;
import com.orientechnologies.common.thread.OPollerThread;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Default page to display an {@link ODocument}
 */
@MountPath("/doc/#{rid}/#{mode}")
public class ODocumentPage extends AbstractODocumentPage {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private TabsPanel<String> tabsPanel;
	private OrienteerStructureTable<ODocument, OProperty> propertiesStructureTable;
	private WebMarkupContainer extendedPropertiesContainer;
	private SaveODocumentCommand saveODocumentCommand;

	private IModel<String> tabModel;
	private IModel<DisplayMode> displayMode = DisplayMode.VIEW.asModel();

	@Inject
	private IOClassIntrospector oClassIntrospector;

	public ODocumentPage(ODocument doc)
	{
		this(new ODocumentModel(doc));
	}

	public ODocumentPage(IModel<ODocument> model) {
		super(model);
	}

	public ODocumentPage(PageParameters parameters) {
		super(parameters);
		DisplayMode mode = DisplayMode.parse(parameters.get("mode").toOptionalString());
		if(mode!=null) displayMode.setObject(mode);
		String tab = parameters.get("tab").toOptionalString();
		tabModel.setObject(tab);
	}

	@Override
	public void initialize() {
		super.initialize();
		tabModel = Model.of();
        tabsPanel = new TabsPanel<String>("tabs", tabModel, new LoadableDetachableModel<List<String>>() {

			@Override
			protected List<String> load() {
				return oClassIntrospector.listTabs(getDocument().getSchemaClass());
			}
		})
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onTabClick(AjaxRequestTarget target) {
				target.add(propertiesStructureTable);
				target.add(extendedPropertiesContainer);
			}

		};
		add(tabsPanel);

		Form<ODocument> form = new Form<ODocument>("form", getModel());
		IModel<List<? extends OProperty>> propertiesModel = new LoadableDetachableModel<List<? extends OProperty>>() {
			@Override
			protected List<? extends OProperty> load() {
				return oClassIntrospector.listProperties(getDocument().getSchemaClass(), tabModel.getObject(), false);
			}
		};
		propertiesStructureTable = new OrienteerStructureTable<ODocument, OProperty>("properties", getModel(), propertiesModel){

					@Override
					protected Component getValueComponent(String id,
							IModel<OProperty> rowModel) {
						return new ODocumentMetaPanel<Object>(id, displayMode, getDocumentModel(), rowModel);
					}
		};
		form.add(propertiesStructureTable);
		add(form);

		//Extended components
		propertiesModel = new LoadableDetachableModel<List<? extends OProperty>>() {
			@Override
			protected List<? extends OProperty> load() {
				return oClassIntrospector.listProperties(getDocument().getSchemaClass(), tabModel.getObject(), true);
			}
		};
		extendedPropertiesContainer = new WebMarkupContainer("extendedPropertiesContainer");
		extendedPropertiesContainer.setOutputMarkupPlaceholderTag(true);
		ListView<OProperty> extendedPropertiesListView = new ListView<OProperty>("extendedProperties", propertiesModel) {

			@Override
			protected void populateItem(ListItem<OProperty> item) {
				Form<?> form = new Form<Object>("form");
				OProperty oProperty = item.getModelObject();
				String component = CustomAttributes.VISUALIZATION_TYPE.getValue(oProperty);
				form.add(OrienteerWebApplication.get()
							.getUIVisualizersRegistry()
							.getComponentFactory(oProperty.getType(), component)
							.createComponent("component", DisplayMode.VIEW, getDocumentModel(), 
												item.getModel(), 
												new DynamicPropertyValueModel<Object>(getDocumentModel(), item.getModel())));
				item.add(form);
			}
		};
		extendedPropertiesContainer.add(extendedPropertiesListView);
		add(extendedPropertiesContainer);
	}
	
	public DisplayMode getDisplayMode()
	{
		return displayMode.getObject();
	}

	public ODocumentPage setDisplayMode(DisplayMode displayMode)
	{
		this.displayMode.setObject(displayMode);
		return this;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		if(getModelObject()==null) throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
		String defaultTab = CustomAttributes.TAB.<String>getValue(getDocument().getSchemaClass(),IOClassIntrospector.DEFAULT_TAB);
		tabsPanel.setDefaultTabModel(Model.of(defaultTab));
		propertiesStructureTable.addCommand(new EditODocumentCommand(propertiesStructureTable, displayMode));
		propertiesStructureTable.addCommand(saveODocumentCommand = new SaveODocumentCommand(propertiesStructureTable, displayMode));
	}



	@Override
	protected void onConfigure() {
		super.onConfigure();
		if(DisplayMode.EDIT.equals(displayMode.getObject()))
		{
			saveODocumentCommand.configure();
			if(!saveODocumentCommand.determineVisibility())
			{
				displayMode.setObject(DisplayMode.VIEW);
			}
		}
//        if (tabModel.getObject()==null)
//        tabModel.setObject(CustomAttributes.TAB.<String>getValue(getDocument().getSchemaClass(),IOClassIntrospector.DEFAULT_TAB));
	}

	@Override
	public IModel<String> getTitleModel() {
		return new ODocumentNameModel(getDocumentModel());
	}

	@Override
	protected Component newPageHeaderComponent(String componentId) {
		return new ODocumentPageHeader(componentId, getModel());
	}



}
