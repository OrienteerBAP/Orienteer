package ru.ydn.orienteer.components.properties;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.model.DynamicPropertyValueModel;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class MetaPanel<V> extends AbstractEntityAndPropertyAwarePanel<ODocument, OProperty, V> {
	private static final String PANEL_ID = "panel";
	private String stateSignature;
	private IModel<DisplayMode> modeModel;
	public MetaPanel(String id, IModel<ODocument> documentModel,
			IModel<OProperty> propertyModel, IModel<DisplayMode> modeModel) {
		super(id, documentModel, propertyModel);
		this.modeModel = modeModel;
	}
	
	@Override
	protected IModel<V> resolveValueModel() {
		return new DynamicPropertyValueModel<V>(getEntityModel(), getPropertyModel());
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
	protected Component resolvePanel(OProperty property, DisplayMode mode)
	{
		if(mode.canModify() && property.isReadonly()) mode = DisplayMode.VIEW;
		if(DisplayMode.VIEW.equals(mode))
		{
			switch(property.getType())
			{
				case LINK:
					return new LinkViewPanel(PANEL_ID, (IModel<OIdentifiable>)getValueModel());
				case LINKLIST:
				case LINKSET:
					return new LinksCollectionViewPanel(PANEL_ID, getValueModel());
				default:
					return new Label(PANEL_ID, getValueModel());
			}
		}
		else if(DisplayMode.EDIT.equals(mode))
		{
			switch(property.getType())
			{
				default:
					return new Label(PANEL_ID, getValueModel());
			}
		}
		else
		{
			return new Label(PANEL_ID, getValueModel());
		}
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		modeModel.detach();
	}
	
	
	
}
