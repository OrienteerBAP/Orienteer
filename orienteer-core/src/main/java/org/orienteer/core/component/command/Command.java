package org.orienteer.core.component.command;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapSize;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.IBootstrapAware;
import org.orienteer.core.component.ICommandsSupportComponent;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.structuretable.StructureTableCommandsToolbar;
import org.orienteer.core.component.table.DataTableCommandsToolbar;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.event.ActionPerformedEvent;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

/**
 * Main class for all commands
 *
 * @param <T> the type of an entity to which this command can be applied
 */
public abstract class Command<T> extends GenericPanel<T> implements IBootstrapAware
{
	private static final AttributeModifier DISABLED_LINK_BEHAVIOR = new AttributeModifier("disabled", AttributeModifier.VALUELESS_ATTRIBUTE_ADD)
	{
		@Override
		public boolean isEnabled(Component component) {
			return !component.isEnabledInHierarchy();
		}
	};
	private static final long serialVersionUID = 1L;
	private IModel<?> labelModel;
	private String icon;
	private AbstractLink link;
	private String btnCssClass;
	private BootstrapType bootstrapType = BootstrapType.DEFAULT;
	private BootstrapSize bootstrapSize = BootstrapSize.DEFAULT;
	private boolean changingModel=false;
	private boolean changingDisplayMode=false;
	private boolean autoNotify=true;
	
	@SuppressWarnings("unchecked")
	public Command(IModel<?> labelModel, ICommandsSupportComponent<T> component)
    {
        this(labelModel, component,
        		component instanceof Component ? (IModel<T>)((Component)component).getDefaultModel() : null);
    }
	
	public Command(IModel<?> labelModel, ICommandsSupportComponent<T> component, IModel<T> model)
    {
        this(component.newCommandId(), labelModel, model);
    }
    
    public Command(String commandId, String labelKey)
    {
        this(commandId, labelKey, null);
    }
    
    public Command(String commandId, String labelKey, IModel<T> model)
    {
        this(commandId, new ResourceModel(labelKey), model);
    }
    
    public Command(String commandId, IModel<?> labelModel)
    {
    	this(commandId, labelModel, null);
    }

    public Command(String commandId, IModel<?> labelModel, IModel<T> model)
    {
        super(commandId, model);
        this.labelModel = labelModel;
        onInstantiation();
    }
    
    protected void onInstantiation() {
    	
    }
    
    /**
     * We are initializing link in onInitialize() because of some links we need to know a structure
     */
    @Override
    protected void onInitialize() {
    	super.onInitialize();
    	link = newLink("command");
        link.setOutputMarkupId(true);
        link.add(new AttributeAppender("class", new PropertyModel<String>(this, "btnCssClass"), " "));
        link.add(new Label("label", labelModel).setRenderBodyOnly(true));
        link.add(new FAIcon("icon", new PropertyModel<String>(this, "icon")));
        link.add(DISABLED_LINK_BEHAVIOR);
        add(link);
    }
    
    @Override
	public Command<T> add(Behavior... behaviors) {
		super.add(behaviors);
		return this;
	}

	protected AbstractLink newLink(String id)
    {
    	return new Link<Object>(id)
        {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void onClick()
            {
                Command.this.onClick();
                trySendActionPerformed();
            }
        };
    }
    
    public String getIcon() {
		return icon;
	}

	public Command<T> setIcon(String icon) {
		this.icon = icon;
		return this;
	}
	
	public Command<T> setIcon(FAIconType type)
	{
		this.icon = type!=null?type.getCssClass():null;
		return this;
	}

	public AbstractLink getLink()
    {
    	return link;
    }
	
	public IModel<?> getLabelModel() {
		return link!=null?link.get("label").getDefaultModel():labelModel;
	}
	
	public Command<T> setLabelModel(IModel<?> labelModel) {
		if(link!=null) link.get("label").setDefaultModel(labelModel);
		else this.labelModel = labelModel;
		return this;
	}
	
    public String getBtnCssClass() {
    	if(btnCssClass!=null) return btnCssClass;
    	else {
    		BootstrapType type = getBootstrapType();
    		if(type==null) return null;
			StringBuilder sb = new StringBuilder();
			sb.append("btn ").append(type.getBtnCssClass());
			BootstrapSize size = getBootstrapSize();
			if(size!=null) sb.append(' ').append(size.getBtnCssClass());
			return sb.toString();
    	}
	}

	public void setBtnCssClass(String btnCssClass) {
		this.btnCssClass = btnCssClass;
	}

	@Override
	public Command<T> setBootstrapType(BootstrapType type) {
    	this.bootstrapType = type;
		return this;
	}

	@Override
	public BootstrapType getBootstrapType() {
		return bootstrapType;
	}
	
    @Override
	public Command<T> setBootstrapSize(BootstrapSize size) {
    	this.bootstrapSize = size;
		return this;
	}

	@Override
	public BootstrapSize getBootstrapSize() {
		return bootstrapSize;
	}
	
	public ODatabaseDocument	getDatabase()
	{
		return OrientDbWebSession.get().getDatabase();
	}
	
	public OSchema getSchema()
	{
		return getDatabase().getMetadata().getSchema();
	}
	
	public boolean isChangingModel() {
		return changingModel;
	}
	
	public Command<T> setChandingModel(boolean changingModel) {
		this.changingModel = changingModel;
		return this;
	}
	
	public boolean isChangingDisplayMode() {
		return changingDisplayMode;
	}
	
	public Command<T> setChangingDisplayMode(boolean changingDisplayMode) {
		this.changingDisplayMode = changingDisplayMode;
		return this;
	}
	
	public boolean isAutoNotify() {
		return autoNotify;
	}

	public Command<T> setAutoNotify(boolean autoNotify) {
		this.autoNotify = autoNotify;
		return this;
	}
	
	/**
	 * Send {@link ActionPerformedEvent} only of auto notify is enabled
	 */
	protected void trySendActionPerformed() {
		if(isAutoNotify()) sendActionPerformed();
	}
	
	protected void sendActionPerformed() {
		send(this, Broadcast.BUBBLE, newActionPerformedEvent());
	}
	
	protected ActionPerformedEvent<T> newActionPerformedEvent() {
		return new ActionPerformedEvent<T>(this);
	}

	public abstract void onClick();
}
