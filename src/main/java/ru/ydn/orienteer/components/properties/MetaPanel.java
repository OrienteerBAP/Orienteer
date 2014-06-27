package ru.ydn.orienteer.components.properties;

import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class MetaPanel<T> extends PropertyViewPanel<T> {
	private static final String PANEL_ID = "panel";
	private String stateSignature;
	private IModel<DisplayMode> modeModel;
	public MetaPanel(String id, IModel<ODocument> documentModel,
			IModel<OProperty> propertyModel, IModel<DisplayMode> modeModel) {
		super(id, documentModel, propertyModel);
		this.modeModel = modeModel;
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		OProperty property = getPropertyModel().getObject();
		DisplayMode mode = modeModel.getObject();
		String newSignature = calcSignature(property, mode);
		if(newSignature.equals(stateSignature) || get(PANEL_ID)==null)
		{
			stateSignature = newSignature;
			addOrReplace(resolvePanel(property, mode).setRenderBodyOnly(true));
		}
	}
	
	protected String calcSignature(OProperty property, DisplayMode mode)
	{
		return (property!=null?property.getType():"null")+"|"+mode;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected PropertyViewPanel<T> resolvePanel(OProperty property, DisplayMode mode)
	{
		if(mode.canModify() && property.isReadonly()) mode = DisplayMode.VIEW;
		if(DisplayMode.VIEW.equals(mode))
		{
			switch(property.getType())
			{
				case LINK:
					return (PropertyViewPanel<T>)new LinkViewPanel(PANEL_ID, getDocumentModel(), getPropertyModel());
				case LINKLIST:
				case LINKSET:
					return (PropertyViewPanel<T>)new LinksCollectionViewPanel(PANEL_ID, getDocumentModel(), getPropertyModel());
				default:
					return (PropertyViewPanel<T>)new DefaultViewPanel(PANEL_ID, getDocumentModel(), getPropertyModel());
			}
		}
		else if(DisplayMode.EDIT.equals(mode))
		{
			switch(property.getType())
			{
				default:
					return (PropertyViewPanel<T>)new DefaultViewPanel(PANEL_ID, getDocumentModel(), getPropertyModel());
			}
		}
		else
		{
			return (PropertyViewPanel<T>)new DefaultViewPanel(PANEL_ID, getDocumentModel(), getPropertyModel());
		}
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		modeModel.detach();
	}
	
	
	
}
