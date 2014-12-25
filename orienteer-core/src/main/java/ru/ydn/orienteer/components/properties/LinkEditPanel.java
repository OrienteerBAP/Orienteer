package ru.ydn.orienteer.components.properties;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

import ru.ydn.orienteer.components.ODocumentPageLink;
import ru.ydn.orienteer.components.commands.modal.SelectDialogPanel;
import ru.ydn.orienteer.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class LinkEditPanel extends FormComponentPanel<ODocument>
{
	protected IModel<ODocument> inputDocument;
	protected ModalWindow modal;

	public LinkEditPanel(String id, IModel<ODocument> documentModel, IModel<OProperty> propertyModel)
	{
		super(id, new DynamicPropertyValueModel<ODocument>(documentModel, propertyModel));
		setOutputMarkupPlaceholderTag(true);
		setRenderBodyOnly(false);
		inputDocument = new ODocumentModel(getModelObject());
		add(new ODocumentPageLink("link", inputDocument).setDocumentNameAsBody(true));
		
		
		modal = new ModalWindow("modal");
		modal.setAutoSize(true);
		add(modal);
		modal.setTitle(new ResourceModel("command.select.modal.title"));
		modal.setContent(new SelectDialogPanel(modal.getContentId(), modal, new PropertyModel<OClass>(propertyModel, "linkedClass")) {
			
			@Override
			protected boolean onSelect(AjaxRequestTarget target, List<ODocument> objects) {
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
	protected void convertInput() {
		super.convertInput();
		setConvertedInput(inputDocument.getObject());
	}

	@Override
	public void detachModels() {
		super.detachModels();
		inputDocument.detach();
	}

}
