package org.orienteer.core.component.table;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.core.component.ICommandsSupportComponent;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.method.MethodPanel;
import org.orienteer.core.method.MethodPlace;

/**
 * {@link AbstractToolbar} to collect {@link Command}s
 *
 * @param <T> the type of a data table objects
 */
public class DataTableCommandsToolbar<T> extends AbstractToolbar implements ICommandsSupportComponent<T>
{
	private static final long serialVersionUID = 1L;
	private RepeatingView commands;
	private MethodPanel methodPanel;
    public DataTableCommandsToolbar(DataTable<T, ?> table)
    {
        super(table);
        WebMarkupContainer span = new WebMarkupContainer("span");
        span.add(new AttributeModifier("colspan", new Model<String>(String.valueOf(table.getColumns().size()))));
        commands = new RepeatingView("commands");
        span.add(commands);
        add(span);
        
		methodPanel = new MethodPanel("methodPanel", table.getDefaultModel(),MethodPlace.DATA_TABLE);
		span.add(methodPanel);
    }

    @Override
	public DataTableCommandsToolbar<T> addCommand(Command<T> command) {
		commands.add(command);
		return this;
	}

	@Override
	public DataTableCommandsToolbar<T> removeCommand(Command<T> command) {
		commands.remove(command);
		return this;
	}

	@Override
	public String newCommandId() {
		return commands.newChildId();
	}

	@SuppressWarnings("unchecked")
	@Override
	public DataTable<T, ?> getTable() {
		return (DataTable<T, ?>)super.getTable();
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		Boolean ret = commands.visitChildren(new IVisitor<Component, Boolean>()
		        {
		            public void component(Component component, IVisit<Boolean> visit)
		            {
		            	component.configure();
		                if(component.determineVisibility())
		                {
		                    visit.stop(true);
		                }
		                else
		                {
		                	visit.dontGoDeeper();
		                }
		            }
		        });
		setVisible(ret!=null?ret:false);
	}

}