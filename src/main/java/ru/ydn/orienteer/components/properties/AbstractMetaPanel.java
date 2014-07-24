package ru.ydn.orienteer.components.properties;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.ILabelProvider;
import org.apache.wicket.markup.html.form.LabeledWebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Objects;
import org.apache.wicket.util.visit.ClassVisitFilter;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import ru.ydn.orienteer.services.IMarkupProvider;

import com.google.inject.Inject;

public abstract class AbstractMetaPanel<T, C, V> extends AbstractEntityAndPropertyAwarePanel<T, C, V> implements ILabelProvider<String>
{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String PANEL_ID = "panel";
	
	private Serializable stateSignature;
	
	private IModel<String> labelModel;
	
	@Inject
	private IMarkupProvider markupProvider;
	
	private Component component;
	
	
	
	public AbstractMetaPanel(String id, IModel<T> entityModel,
			IModel<C> propertyModel, IModel<V> valueModel)
	{
		super(id, entityModel, propertyModel, valueModel);
	}

	public AbstractMetaPanel(String id, IModel<T> entityModel,
			IModel<C> propertyModel)
	{
		super(id, entityModel, propertyModel);
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		
		C critery = getPropertyObject();
		Serializable newSignature = getSignature(critery);
		if(!newSignature.equals(stateSignature) || get(PANEL_ID)==null)
		{
			stateSignature = newSignature;
			component = resolveComponent(PANEL_ID, critery);
			if(component instanceof LabeledWebMarkupContainer)
			{
				((LabeledWebMarkupContainer)component).setLabel(getLabel());
			}
//			component.setOutputMarkupId(true);
			addOrReplace(component);
		}
	}
	
	protected Serializable getSignature(C critery)
	{
		return Objects.hashCode(critery);
	}
	
	@Override
	public IMarkupFragment getMarkup(Component child) {
		if(child==null) return super.getMarkup(child);
		IMarkupFragment ret = markupProvider.provideMarkup(child);
		return ret!=null?ret:super.getMarkup(child);
	}
	
	@Override
	public IModel<String> getLabel() {
		if(labelModel==null)
		{
			labelModel = newLabelModel();
		}
		return labelModel;
	}
	
	@SuppressWarnings("unchecked")
	public IMetaContext<C> getMetaContext()
	{
		return (IMetaContext<C>) visitParents(MarkupContainer.class, new IVisitor<MarkupContainer, IMetaContext<C>>() {

			@Override
			public void component(MarkupContainer object,
					IVisit<IMetaContext<C>> visit) {
				visit.stop((IMetaContext<C>)object);
			}
		}, new ClassVisitFilter(IMetaContext.class));
	}
	
	public <V2> AbstractMetaPanel<T, C, V2> getMetaComponent(C critery)
	{
		return getMetaComponent(getMetaContext(), critery);
	}
	
	public <V2> V2 getMetaComponentValue(C critery)
	{
		AbstractMetaPanel<T, C, V2> otherMetaPanel = getMetaComponent(critery);
		return otherMetaPanel!=null?otherMetaPanel.getValueObject():null;
	}
	
	@SuppressWarnings("unchecked")
	public V getEnteredValue()
	{
		if(component instanceof FormComponent)
		{
			return ((FormComponent<V>)component).getConvertedInput();
		}
		else
		{
			return getValueObject();
		}
	}
	
	public <V2> V2 getMetaComponentEnteredValue(C critery)
	{
		AbstractMetaPanel<T, C, V2> otherMetaPanel = getMetaComponent(critery);
		if(otherMetaPanel==null) return null;
		return otherMetaPanel!=null?otherMetaPanel.getEnteredValue():null;
	}
	
	@SuppressWarnings("unchecked")
	public static <K extends AbstractMetaPanel<?, ?, ?>> K getMetaComponent(IMetaContext<?> context, final Object critery)
	{
		if(context==null || critery==null) return null;
		else return (K)context.getContextComponent()
						.visitChildren(AbstractMetaPanel.class, new IVisitor<AbstractMetaPanel<?, ?, ?>, AbstractMetaPanel<?, ?, ?>>() {

							@Override
							public void component(
									AbstractMetaPanel<?, ?, ?> object,
									IVisit<AbstractMetaPanel<?, ?, ?>> visit) {
								if(Objects.isEqual(object.getPropertyObject(), critery)) visit.stop(object);
								else visit.dontGoDeeper();
							}
		});
	}

	protected abstract IModel<String> newLabelModel();
	protected abstract Component resolveComponent(String id, C critery);
	
}
