package ru.ydn.orienteer.components;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.model.DocumentNameModel;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ODocumentPageLink extends BookmarkablePageLink<OIdentifiable>
{
	public ODocumentPageLink(String id, IModel<OIdentifiable> docModel, PageParameters parameters)
	{
		this(id, docModel, DisplayMode.VIEW, parameters);
	}
	
	public ODocumentPageLink(String id, IModel<OIdentifiable> docModel)
	{
		this(id, docModel, DisplayMode.VIEW);
	}
	
	public ODocumentPageLink(String id, IModel<OIdentifiable> docModel, DisplayMode mode, PageParameters parameters)
	{
		this(id, docModel, mode.getDefaultPageClass(), parameters);
	}
	
	public ODocumentPageLink(String id, IModel<OIdentifiable> docModel, DisplayMode mode)
	{
		this(id, docModel, mode.getDefaultPageClass());
	}
	public <C extends Page> ODocumentPageLink(String id, IModel<OIdentifiable> docModel, Class<C> pageClass,
			PageParameters parameters) {
		super(id, pageClass, parameters);
		setModel(docModel);
	}

	public <C extends Page> ODocumentPageLink(String id, IModel<OIdentifiable> docModel, Class<C> pageClass) {
		super(id, pageClass);
		setModel(docModel);
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisible(getModelObject()!=null);
	}

	public ODocumentPageLink setDocumentNameAsBody(boolean docNameAsBody)
	{
		setBody(docNameAsBody?new DocumentNameModel(getModel()):null);
		return this;
	}

	@Override
	public PageParameters getPageParameters() {
		return super.getPageParameters().add("rid", buitifyRid(getModelObject()));
	}
	
	public String buitifyRid(OIdentifiable identifiable)
	{
		if(identifiable==null) return "";
		String ret = identifiable.getIdentity().toString();
		return ret.charAt(0)==ORID.PREFIX?ret.substring(1):ret;
	}

}
