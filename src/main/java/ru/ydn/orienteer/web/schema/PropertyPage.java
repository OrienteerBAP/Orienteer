package ru.ydn.orienteer.web.schema;

import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.annotation.mount.MountPath;

import ru.ydn.orienteer.components.commands.EditCommand;
import ru.ydn.orienteer.components.commands.SchemaSaveCommand;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.properties.OPropertyMetaPanel;
import ru.ydn.orienteer.components.structuretable.OrienteerStructureTable;
import ru.ydn.orienteer.web.OrienteerBasePage;
import ru.ydn.wicket.wicketorientdb.model.AbstractNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;

@MountPath("/property/${className}/${propertyName}")
@RequiredOrientResource(value=ODatabaseSecurityResources.SCHEMA, permissions=OrientPermission.READ)
public class PropertyPage extends OrienteerBasePage<OProperty>
{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

private static String[] ATTRS_TO_VIEW = new String[]{"name", "type", "linkedType", "linkedClass", "mandatory", "readonly", "notNull", "min", "max", "collate"};
	
	private OrienteerStructureTable<OProperty, String> structureTable;
	
	private IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
	
	public PropertyPage(IModel<OProperty> model) {
		super(model);
	}

	public PropertyPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	protected IModel<OProperty> resolveByPageParameters(
			PageParameters pageParameters) {
		String className = pageParameters.get("className").toOptionalString();
		String propertyName = pageParameters.get("propertyName").toOptionalString();
		return Strings.isEmpty(className) || Strings.isEmpty(propertyName)?null:new OPropertyModel(className, propertyName) ;
	}

	@Override
	public void initialize() {
		super.initialize();
		Form<OProperty> form = new Form<OProperty>("form");
		structureTable  = new OrienteerStructureTable<OProperty, String>("attributes", Arrays.asList(ATTRS_TO_VIEW)) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected IModel<?> getLabelModel(IModel<String> rowModel) {
				return new AbstractNamingModel<String>(rowModel) {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public String getResourceKey(String object) {
						return "property."+object;
					}
				};
			}

			@Override
			protected Component getValueComponent(String id, final IModel<String> rowModel) {
				return new OPropertyMetaPanel<Object>(id, modeModel, rowModel, new PropertyModel<Object>(PropertyPage.this.getModel(), rowModel.getObject()));
			}
		};
		
		form.add(structureTable);
		
		add(form);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		structureTable.addCommand(new EditCommand<OProperty>(structureTable, modeModel));
		structureTable.addCommand(new SchemaSaveCommand<OProperty>(structureTable, modeModel));
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
}
