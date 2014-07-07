package ru.ydn.orienteer.components.properties;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.components.IMetaComponentResolver;
import ru.ydn.orienteer.services.IMarkupProvider;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

public abstract class AbstractMetaPanel<T, C, V> extends GenericPanel<V>
{
	private static final String PANEL_ID = "panel";
	
	private Serializable stateSignature;
	private  IModel<C> criteryModel;
	
	@Inject
	private IMarkupProvider markupProvider;
	
	public AbstractMetaPanel(String id, IModel<C> criteryModel, IModel<V> model) {
		super(id, model);
		this.criteryModel = criteryModel;
	}

	public AbstractMetaPanel(String id, IModel<C> criteryModel) {
		super(id);
		this.criteryModel = criteryModel;
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		C critery = criteryModel.getObject();
		IMetaComponentResolver<C> resolver = getComponentResolver();
		Serializable newSignature = subSign(resolver.getSignature(critery));
		if(!newSignature.equals(stateSignature) || get(PANEL_ID)==null)
		{
			stateSignature = newSignature;
			addOrReplace(resolver.resolve(PANEL_ID, critery));
		}
	}
	
	protected Serializable subSign(Serializable thisSignature)
	{
		return thisSignature;
	}
	
	
	
	@Override
	public IMarkupFragment getMarkup(Component child) {
		if(child==null) return super.getMarkup(child);
		IMarkupFragment ret = markupProvider.provideMarkup(child);
		return ret!=null?ret:super.getMarkup(child);
	}

	protected abstract IMetaComponentResolver<C> getComponentResolver();

}
