package org.orienteer.core.component.structuretable;

import org.apache.wicket.Component;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.core.component.ICommandsSupportComponent;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.method.MethodsView;
import org.orienteer.core.method.MethodPlace;

/**
 * {@link AbstractStructureTableToolbar} to add {@link Command}s
 *
 * @param <P> the type of main object for a commands
 */
public class StructureTableCommandsToolbar<P> extends
		AbstractStructureTableToolbar<P> implements ICommandsSupportComponent<P>
{
	private static final long serialVersionUID = 1L;
	private RepeatingView commands;
	private MethodsView methods;

	public StructureTableCommandsToolbar(StructureTable<P, ?> table)
	{
		super(table);
        commands = new RepeatingView("commands");
        add(commands);
		methods = new MethodsView("methods", getModel(),MethodPlace.STRUCTURE_TABLE);
		add(methods);

	}
	
	@Override
	public StructureTableCommandsToolbar<P> addCommand(Command<P> command) {
		commands.add(command);
        return this;
	}

	@Override
	public StructureTableCommandsToolbar<P> removeCommand(Command<P> command) {
		commands.remove(command);
        return this;
	}

	@Override
	public String newCommandId() {
		return commands.newChildId();
	}

    @Override
	protected void onConfigure() {
		super.onConfigure();
		IVisitor<Component, Boolean> visitor = new IVisitor<Component, Boolean>()
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
        };
		Boolean ret = commands.visitChildren(visitor);
		if (ret==null || !ret){
			ret = methods.visitChildren(visitor);
		}
		setVisible(ret!=null?ret:false);
	}


}
