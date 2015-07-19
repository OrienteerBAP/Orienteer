package org.orienteer.core.component.command;

import java.util.Collection;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.modal.SelectDialogPanel;
import org.orienteer.core.component.table.OrienteerDataTable;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link Command} to select (establish new link to a document) some {@link ODocument}
 */
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
		boolean multiValue = propertyModel.getObject().getType().isMultiValue();
		modal.setContent(new SelectDialogPanel(modal.getContentId(), modal, new PropertyModel<OClass>(propertyModel, "linkedClass"), multiValue) {

			@Override
			protected boolean onSelect(AjaxRequestTarget target, List<ODocument> objects, boolean selectMore) {
				if(objects==null || objects.isEmpty()) return true;
				OType oType = propertyModel.getObject().getType();
				
				if(!oType.isMultiValue() && objects.size()>1)
				{
					String message = getLocalizer().getString("alert.onlyoneshouldbeselected", this).replace("\"", "\\\"");
					target.appendJavaScript("alert(\""+message+"\")");
					return false;
				}
				
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
				}
				else
				{
					ODocument doc = documentModel.getObject();
					OProperty property = propertyModel.getObject();
					doc.field(property.getName(), objects.get(0));
					doc.save();
					return true;
				}

				if (!selectMore) {
					send(SelectODocumentCommand.this, Broadcast.BUBBLE, target);
				}
				return true;
			}
		});

		modal.setCloseButtonCallback(new ModalWindow.CloseButtonCallback() {
			@Override
			public boolean onCloseButtonClicked(AjaxRequestTarget target) {
				send(SelectODocumentCommand.this, Broadcast.BUBBLE, target);
				return true;
			}
		});
	}

}
