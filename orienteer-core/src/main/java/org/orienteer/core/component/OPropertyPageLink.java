package org.orienteer.core.component;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.web.schema.OPropertyPage;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

/**
 * {@link BookmarkablePageLink} for {@link OProperty}
 */
public class OPropertyPageLink extends BookmarkablePageLink<OProperty>
{
	private static final long serialVersionUID = 1L;
	private IModel<DisplayMode> displayModeModel;
	public OPropertyPageLink(String id, IModel<OProperty> oClassModel, PageParameters parameters)
	{
		this(id, oClassModel, DisplayMode.VIEW, parameters);
	}
	
	public OPropertyPageLink(String id, IModel<OProperty> oClassModel)
	{
		this(id, oClassModel, DisplayMode.VIEW);
	}
	
	public OPropertyPageLink(String id, IModel<OProperty> oClassModel, DisplayMode mode, PageParameters parameters)
	{
		this(id, oClassModel, resolvePageClass(mode), mode.asModel(), parameters);
	}
	
	public OPropertyPageLink(String id, IModel<OProperty> oClassModel, DisplayMode mode)
	{
		this(id, oClassModel, resolvePageClass(mode), mode.asModel());
	}
	public <C extends Page> OPropertyPageLink(String id, IModel<OProperty> oClassModel, Class<C> pageClass, 
			IModel<DisplayMode> displayModeModel, PageParameters parameters) {
		super(id, pageClass, parameters);
		setModel(oClassModel);
		this.displayModeModel = displayModeModel;
	}
	

	public <C extends Page> OPropertyPageLink(String id, IModel<OProperty> oClassModel, Class<C> pageClass,
			IModel<DisplayMode> displayModeModel) {
		super(id, pageClass);
		setModel(oClassModel);
		this.displayModeModel = displayModeModel;
	}
	
	private static Class<? extends Page> resolvePageClass(DisplayMode mode)
	{
		switch (mode) {
		case VIEW:
			return OPropertyPage.class;
		case EDIT:
			return OPropertyPage.class;
		default:
			return OPropertyPage.class;
		}
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisible(getModelObject()!=null);
	}

	public OPropertyPageLink setPropertyNameAsBody(boolean classNameAsBody)
	{
		setBody(classNameAsBody?new PropertyModel<String>(getModel(), "name"):null);
		return this;
	}
	
	
	@Override
	public PageParameters getPageParameters() {
		return super.getPageParameters().add("className", getModelObject().getOwnerClass().getName()).add("propertyName", getModelObject().getName());
	}
}
