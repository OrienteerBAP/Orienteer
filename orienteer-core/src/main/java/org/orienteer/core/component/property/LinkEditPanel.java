package org.orienteer.core.component.property;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.IExportable;
import org.orienteer.core.component.ODocumentPageLink;
import org.orienteer.core.component.command.modal.SelectDialogPanel;
import org.orienteer.core.model.ODocumentNameModel;
import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link FormComponentPanel} to edit LINK properties
 */
public class LinkEditPanel extends FormComponentPanel<OIdentifiable> implements IExportable<String>
{
	protected IModel<ODocument> inputDocument;
	protected ModalWindow modal;
	
	public LinkEditPanel(String id, IModel<ODocument> documentModel, IModel<OProperty> propertyModel) {
		this(id, documentModel, propertyModel, new DynamicPropertyValueModel<OIdentifiable>(documentModel, propertyModel));
	}

	public LinkEditPanel(String id, IModel<ODocument> documentModel, IModel<OProperty> propertyModel, IModel<OIdentifiable> valueModel)
	{
		super(id, valueModel);
		setOutputMarkupPlaceholderTag(true);
		setRenderBodyOnly(false);
		inputDocument = new ODocumentModel(getModelObject());
		add(new ODocumentPageLink("link", inputDocument).setDocumentNameAsBody(true));
		
		
		modal = new ModalWindow("modal");
		modal.setAutoSize(true);
		add(modal);
		modal.setTitle(new ResourceModel("command.select.modal.title"));
		modal.setContent(new SelectDialogPanel(modal.getContentId(), modal, new PropertyModel<OClass>(propertyModel, "linkedClass"), false) {

			@Override
			protected boolean onSelect(AjaxRequestTarget target, List<ODocument> objects, boolean selectMore) {
				if(objects==null || objects.size()==0) return true;
				if(objects.size()>1)
				{
					String message = getLocalizer().getString("alert.onlyoneshouldbeselected", this).replace("\"", "\\\"");
					target.appendJavaScript("alert(\""+message+"\")");
					return false;
				}
				else
				{
					inputDocument.setObject(objects.get(0));
					target.add(LinkEditPanel.this);
					return true;
				}
			}
		});
		
		add(new AjaxLink("select") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				modal.show(target);
			}
		});
		
		add(new AjaxLink("release") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				inputDocument.setObject(null);
				target.add(LinkEditPanel.this);
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(inputDocument.getObject()!=null);
			}
			
			
		});
	}
	
	@Override
	public IModel<String> getExportableDataModel() {
		return new ODocumentNameModel(inputDocument);
	}

	@Override
	public void convertInput() {
		super.convertInput();
		setConvertedInput(inputDocument.getObject());
	}

	@Override
	public void detachModels() {
		super.detachModels();
		inputDocument.detach();
	}

}
