package ru.ydn.orienteer.components.commands;

import java.util.Collection;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.commands.modal.SelectDialogPanel;
import ru.ydn.orienteer.components.table.OrienteerDataTable;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class SelectODocumentCommand extends AbstractModalWindowCommand<ODocument>
{
	private IModel<ODocument> documentModel;
	private IModel<OProperty> propertyModel;
	
	public SelectODocumentCommand(OrienteerDataTable<ODocument, ?> table, IModel<ODocument> documentModel, IModel<OProperty> propertyModel)
	{
		super(new ResourceModel("command.select"), table);
		setBootstrapType(BootstrapType.SUCCESS);
		setIcon(FAIconType.hand_o_right);
		this.documentModel = documentModel;
		this.propertyModel = propertyModel;
	}

	@Override
	protected void initializeContent(ModalWindow modal) {
		modal.setTitle(new ResourceModel("command.select.modal.title"));
		modal.setContent(new SelectDialogPanel(modal.getContentId(), modal, new PropertyModel<OClass>(propertyModel, "linkedClass")) {
			
			@Override
			protected boolean onSelect(AjaxRequestTarget target, List<ODocument> objects) {
				if(objects==null || objects.isEmpty()) return true;
				OType oType = propertyModel.getObject().getType();
				if(oType.isMultiValue())
				{
					ODocument doc = documentModel.getObject();
					OProperty property = propertyModel.getObject();
					Collection<ODocument> links = doc.field(property.getName());
					if(links!=null)
					{
						links.addAll(objects);
					}
					else
					{
						doc.field(property.getName(), objects);
					}
					doc.save();
					return true;
				}
				else
				{
					if(objects.size()>1)
					{
						String message = getLocalizer().getString("alert.onlyoneshouldbeselected", this).replace("\"", "\\\"");
						target.appendJavaScript("alert(\""+message+"\")");
						return false;
					}
					else
					{
						ODocument doc = documentModel.getObject();
						OProperty property = propertyModel.getObject();
						doc.field(property.getName(), objects.get(0));
						doc.save();
						return true;
					}
				}
			}
		});
	}

}
