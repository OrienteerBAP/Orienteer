package org.orienteer.core.component.command;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapSize;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.IBootstrapAware;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.structuretable.StructureTableCommandsToolbar;
import org.orienteer.core.component.table.DataTableCommandsToolbar;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.web.schema.OClassPage;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.proto.IPrototype;


public abstract class Command<T> extends Panel implements IBootstrapAware
{
	private static final AttributeModifier DISABLED_LINK_BEHAVIOR = new AttributeModifier("disabled", AttributeModifier.VALUELESS_ATTRIBUTE_ADD)
	{
		@Override
		public boolean isEnabled(Component component) {
			return !component.isEnabledInHierarchy();
		}
	};
	private static final long serialVersionUID = 1L;
	private String icon;
	private AbstractLink link;
	private BootstrapType bootstrapType = BootstrapType.DEFAULT;
	private BootstrapSize bootstrapSize = BootstrapSize.DEFAULT;
	
	public Command(IModel<?> labelModel, StructureTableCommandsToolbar<T> toolbar)
    {
        this(toolbar.newChildId(), labelModel);
    }
	
    public Command(IModel<?> labelModel, DataTableCommandsToolbar<T> toolbar)
    {
        this(toolbar.newChildId(), labelModel);
    }
    
    public Command(IModel<?> labelModel, OrienteerDataTable<T, ?> table)
    {
        this(labelModel, table.getCommandsToolbar());
    }
    
    public Command(IModel<?> labelModel, OrienteerStructureTable<T, ?> table)
    {
        this(labelModel, table.getCommandsToolbar());
    }

    public Command(String labelKey)
    {
        this(labelKey, new ResourceModel(labelKey));
    }

    public Command(String commandId, String labelKey)
    {
        this(commandId, new ResourceModel(labelKey));
    }

    public Command(String commandId, IModel<?> labelModel)
    {
        super(commandId);
        link = newLink("command");
        link.setOutputMarkupId(true);
        link.add(new AttributeAppender("class", new PropertyModel<String>(this, "bootstrapType.btnCssClass"), " "));
        link.add(new AttributeAppender("class", new PropertyModel<String>(this, "bootstrapSize.btnCssClass"), " "));
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

	AbstractLink getLink()
    {
    	return link;
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

	public abstract void onClick();
}
