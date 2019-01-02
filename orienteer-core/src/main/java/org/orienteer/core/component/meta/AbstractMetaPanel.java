package org.orienteer.core.component.meta;

import com.google.inject.Inject;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.ILabelProvider;
import org.apache.wicket.markup.html.form.LabeledWebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.visit.ClassVisitFilter;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.IExportable;
import org.orienteer.core.service.IMarkupProvider;
import org.orienteer.core.service.ISignatureService;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * {@link Panel} that can substitute required component according to a provided criteria
 *
 * @param <T> the type of an entity
 * @param <C> the type of a criteria
 * @param <V> the type of a value
 */
public abstract class AbstractMetaPanel<T, C, V> extends AbstractEntityAndPropertyAwarePanel<T, C, V> implements ILabelProvider<String>, IExportable<Object>
{
	private static final long serialVersionUID = 1L;

	private static final String PANEL_ID = "panel";

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
		String key = getSignatureKey();
		ISignatureService signatureService = OrienteerWebApplication.get().getServiceInstance(ISignatureService.class);
		String oldSignature = signatureService.getSignature(key);
		String newSignature = computeSignature(critery, signatureService);

		if (!Objects.equals(oldSignature, newSignature) || get(PANEL_ID)==null)
		{
			signatureService.putSignature(key, newSignature);
			component = resolveComponent(PANEL_ID, critery);
			onPostResolveComponent(component, critery);
			addOrReplace(component);
		}
	}

	protected void onPostResolveComponent(Component component, C critery)
	{
		if(component instanceof LabeledWebMarkupContainer)
		{
			((LabeledWebMarkupContainer)component).setLabel(getLabel());
		}
	}
	
	protected String computeSignature(C critery, ISignatureService signatureService) {
		return signatureService.computeSignature(getPropertyObject());
	}

	protected String getSignatureKey() {
		String sessionId = getSession() != null ? getSession().getId() : "-1";
		String pageId = getPage().getId();
		return sessionId + ":" + pageId + ":" + getPageRelativePath();
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
		return getMetaContext(this);
	}
	
	@SuppressWarnings("unchecked")
	public static <C> IMetaContext<C> getMetaContext(Component component)
	{
		return (IMetaContext<C>) component.visitParents(MarkupContainer.class, new IVisitor<MarkupContainer, IMetaContext<C>>() {

			@Override
			public void component(MarkupContainer object,
					IVisit<IMetaContext<C>> visit) {
				visit.stop((IMetaContext<C>)object);
			}
		}, new ClassVisitFilter(IMetaContext.class));
	}
	
	public <W> AbstractMetaPanel<T, C, W> getMetaComponent(C critery)
	{
		return getMetaComponent(getMetaContext(), critery);
	}
	
	public <W> W getMetaComponentValue(C critery)
	{
		AbstractMetaPanel<T, C, W> otherMetaPanel = getMetaComponent(critery);
		return otherMetaPanel!=null?otherMetaPanel.getValueObject():null;
	}

	@SuppressWarnings("unchecked")
	public V getEnteredValue()
	{
		if(component instanceof FormComponent && ((FormComponent<V>)component).hasRawInput())
		{
			FormComponent<V> formComponent = (FormComponent<V>)component;
			convertInput(formComponent);
			return formComponent.getConvertedInput();
		}
		else
		{
			return getValueObject();
		}
	}
	
	private void convertInput(FormComponent<V> formComponent)
	{
		try
		{
			Method convertInputMethod = FormComponent.class.getDeclaredMethod("convertInput");
			convertInputMethod.setAccessible(true);
			convertInputMethod.invoke(formComponent);
		} catch (Exception e)
		{
			throw new WicketRuntimeException("Can't invoke 'convertInput' on component", e);
		} 
	}
	
	public <W> W getMetaComponentEnteredValue(C critery)
	{
		AbstractMetaPanel<T, C, W> otherMetaPanel = getMetaComponent(critery);
		if(otherMetaPanel==null) return null;
		return otherMetaPanel!=null?otherMetaPanel.getEnteredValue():null;
	}
	
	public IModel<?> getMetaComponentEnteredValueModel(final C critery)
	{
		return () -> getMetaComponentEnteredValue(critery);
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
								if(Objects.equals(object.getPropertyObject(), critery)) visit.stop(object);
								else visit.dontGoDeeper();
							}
		});
	}
	
	public IModel<?> getExportableDataModel() {
		configure();
		if(component instanceof IExportable){
			return ((IExportable<?>) component).getExportableDataModel();
		} else if(component instanceof Label) {
			return Model.of(component.getDefaultModelObjectAsString());
		} else return getModel();
	}

	protected abstract IModel<String> newLabelModel();
	protected abstract Component resolveComponent(String id, C critery);
	
}
