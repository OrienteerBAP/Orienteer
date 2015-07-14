package org.orienteer.core.component;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.web.schema.NewOClassPage;
import org.orienteer.core.web.schema.OClassPage;

import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * {@link BookmarkablePageLink} for {@link OClass}
 */
public class OClassPageLink extends BookmarkablePageLink<OClass>
{
	private static final long serialVersionUID = 1L;
	private IModel<DisplayMode> displayModeModel;
	public OClassPageLink(String id, IModel<OClass> oClassModel, PageParameters parameters)
	{
		this(id, oClassModel, DisplayMode.VIEW, parameters);
	}
	
	public OClassPageLink(String id, IModel<OClass> oClassModel)
	{
		this(id, oClassModel, DisplayMode.VIEW);
	}
	
	public OClassPageLink(String id, IModel<OClass> oClassModel, DisplayMode mode, PageParameters parameters)
	{
		this(id, oClassModel, resolvePageClass(mode), mode.asModel(), parameters);
	}
	
	public OClassPageLink(String id, IModel<OClass> oClassModel, DisplayMode mode)
	{
		this(id, oClassModel, resolvePageClass(mode), mode.asModel());
	}
	public <C extends Page> OClassPageLink(String id, IModel<OClass> oClassModel, Class<C> pageClass, 
			IModel<DisplayMode> displayModeModel, PageParameters parameters) {
		super(id, pageClass, parameters);
		setModel(oClassModel);
		this.displayModeModel = displayModeModel;
	}
	

	public <C extends Page> OClassPageLink(String id, IModel<OClass> oClassModel, Class<C> pageClass,
			IModel<DisplayMode> displayModeModel) {
		super(id, pageClass);
		setModel(oClassModel);
		this.displayModeModel = displayModeModel;
	}
	
	private static Class<? extends Page> resolvePageClass(DisplayMode mode)
	{
		switch (mode) {
		case VIEW:
			return NewOClassPage.class;
		case EDIT:
			return NewOClassPage.class;
		default:
			return NewOClassPage.class;
		}
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisible(getModelObject()!=null);
	}

	public OClassPageLink setClassNameAsBody(boolean classNameAsBody)
	{
		setBody(classNameAsBody?new PropertyModel<String>(getModel(), "name"):null);
		return this;
	}
	
	
	@Override
	public PageParameters getPageParameters() {
		return super.getPageParameters().add("className", getModelObject().getName());
	}
}
