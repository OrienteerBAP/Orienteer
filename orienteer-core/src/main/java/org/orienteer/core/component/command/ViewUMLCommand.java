package org.orienteer.core.component.command;

import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.modal.ViewUMLDialogPanel;
import org.orienteer.core.component.table.DataTableCommandsToolbar;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.service.IUmlService;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * Command to show UML for a selected {@link OClass}es
 */
public class ViewUMLCommand extends AbstractCheckBoxEnabledModalWindowCommand<OClass>
{
	@Inject
	private IUmlService umlService;

	public ViewUMLCommand(OrienteerDataTable<OClass, ?> table)
	{
		super(new ResourceModel("command.viewUml"), table);
	}
	
	@Override
	protected void onInstantiation() {
		super.onInstantiation();
		setBootstrapType(BootstrapType.INFO);
		setIcon(FAIconType.cubes);
	}

	@Override
	protected void initializeContent(ModalWindow modal) {
		modal.setTitle(new ResourceModel("command.viewUml.modal.title"));
		modal.setContent(new ViewUMLDialogPanel(modal.getContentId(), new PropertyModel<String>(this, "uml")));
		modal.setAutoSize(true);
		modal.setMinimalWidth(600);
		modal.setMinimalHeight(400);
	}
	
	public String getUml()
	{
		List<OClass> selected = getSelected();
		if(selected==null || selected.size()==0)
		{
			return umlService.describe(getSchema());
		}
		else
		{
			return umlService.describe(true, true, selected.toArray(new OClass[selected.size()]));
		}
	}

}
