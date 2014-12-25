package ru.ydn.orienteer.components;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.web.schema.OClassPage;

import com.orientechnologies.orient.core.metadata.schema.OClass;

public class OClassPageLink extends BookmarkablePageLink<OClass>
{
	/**
	 * 
	 */
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
			return OClassPage.class;
		case EDIT:
			return OClassPage.class;
		default:
			return OClassPage.class;
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
