package org.orienteer.core.web.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.MountPath;
import org.orienteer.core.component.SchemaPageHeader;
import org.orienteer.core.component.command.CreateOClassCommand;
import org.orienteer.core.component.command.CreateOPropertyCommand;
import org.orienteer.core.component.command.EditCommand;
import org.orienteer.core.component.command.EditSchemaCommand;
import org.orienteer.core.component.command.SavePrototypeCommand;
import org.orienteer.core.component.command.SaveSchemaCommand;
import org.orienteer.core.component.meta.OPropertyMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.property.OClassViewPanel;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.web.OrienteerBasePage;

import ru.ydn.wicket.wicketorientdb.model.AbstractNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.proto.OPropertyPrototyper;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;

/**
 * Page to show {@link OProperty} specific parameters
 */
@MountPath("/property/${className}/${propertyName}")
@RequiredOrientResource(value=OSecurityHelper.SCHEMA, permissions=OrientPermission.READ)
public class OPropertyPage extends OrienteerBasePage<OProperty>
{
	private static final long serialVersionUID = 1L;

	private OrienteerStructureTable<OProperty, String> structureTable;
	
	private IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
	
	public OPropertyPage(IModel<OProperty> model) {
		super(model);
	}

	public OPropertyPage(PageParameters parameters) {
		super(parameters);
		DisplayMode mode = DisplayMode.parse(parameters.get("mode").toOptionalString());
		if(mode!=null) modeModel.setObject(mode);
	}

	@Override
	protected IModel<OProperty> resolveByPageParameters(
			PageParameters pageParameters) {
		String className = pageParameters.get("className").toOptionalString();
		String propertyName = pageParameters.get("propertyName").toOptionalString();
		return Strings.isEmpty(className) || Strings.isEmpty(propertyName)?null:new OPropertyModel(className, propertyName) ;
	}
	
	public IModel<DisplayMode> getDisplayModeModel() {
		return modeModel;
	}
	
	public DisplayMode getDisplayMode()
	{
		return modeModel.getObject();
	}
	
	public OPropertyPage setDisplayMode(DisplayMode mode)
	{
		modeModel.setObject(mode);
		return this;
	}

	@Override
	public void initialize() {
		super.initialize();
		Form<OProperty> form = new Form<OProperty>("form");
		structureTable  = new OrienteerStructureTable<OProperty, String>("attributes", getModel(), OPropertyMetaPanel.OPROPERTY_ATTRS) {

			@Override
			protected Component getValueComponent(String id, final IModel<String> rowModel) {
				return new OPropertyMetaPanel<Object>(id, modeModel, OPropertyPage.this.getModel(), rowModel);
			}
		};
		
		form.add(structureTable);
		
		add(form);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		structureTable.addCommand(new EditSchemaCommand<OProperty>(structureTable, modeModel));
		structureTable.addCommand(new SaveSchemaCommand<OProperty>(structureTable, modeModel, getModel()));
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		if(getModelObject()==null) throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
	}

	@Override
	public IModel<String> getTitleModel() {
		return new PropertyModel<String>(getModel(), "name");
	}
	
	@Override
	protected Component newPageHeaderComponent(String componentId) {
		SchemaPageHeader pageHeader = new SchemaPageHeader(componentId);
		pageHeader.addChild(new OClassViewPanel(pageHeader.newChildId(), new PropertyModel<OClass>(getModel(), "ownerClass")));
		pageHeader.addChild(new Label(pageHeader.newChildId(), getTitleModel()));
		return pageHeader;
	}
}
