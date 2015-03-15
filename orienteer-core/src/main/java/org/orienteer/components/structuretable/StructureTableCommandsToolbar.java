package org.orienteer.components.structuretable;

import org.apache.wicket.Component;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.components.commands.Command;

public class StructureTableCommandsToolbar<P> extends
		AbstractStructureTableToolbar<P>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RepeatingView commands;

	public StructureTableCommandsToolbar(StructureTable<P, ?> table)
	{
		super(table);
        commands = new RepeatingView("commands");
        add(commands);
	}
	
	public StructureTableCommandsToolbar<P> add(Command<P> command)
    {
        commands.add(command);
        return this;
    }

    public String newChildId()
    {
        return commands.newChildId();
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
