package ru.ydn.orienteer.components.structuretable;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import ru.ydn.orienteer.components.commands.Command;
import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;

public class StructureTableCommandsToolbar extends
		AbstractStructureTableToolbar
{
	private RepeatingView commands;

	public StructureTableCommandsToolbar(StructureTable<?> table)
	{
		super(table);
        commands = new RepeatingView("commands");
        add(commands);
	}
	
	public StructureTableCommandsToolbar add(Command command)
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
