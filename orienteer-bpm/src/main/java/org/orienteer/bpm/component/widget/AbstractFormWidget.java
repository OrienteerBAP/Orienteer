package org.orienteer.bpm.component.widget;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.cmd.GetFormKeyCmd;
import org.camunda.bpm.engine.task.Task;
import org.orienteer.bpm.component.command.CompleteTaskCommand;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.EditODocumentCommand;
import org.orienteer.core.component.command.SaveODocumentCommand;
import org.orienteer.core.component.meta.ODocumentMetaPanel;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.widget.AbstractModeAwareWidget;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.NvlModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;

/**
 * Abstract form widget 
 */
public abstract class AbstractFormWidget extends AbstractModeAwareWidget<ODocument> {

	@Inject
	protected IOClassIntrospector oClassIntrospector;
	
	protected FormKey formKey;
	protected ODocumentModel formDocumentModel;
	protected OrienteerStructureTable<ODocument, OProperty> propertiesStructureTable;
	
	
	public AbstractFormWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		formKey = obtainFormKey();
		String formKeyStr = formKey.toString();
		add(new Label("formKey", formKeyStr));
		setVisible(formKey.isValid());
		formDocumentModel = new ODocumentModel(resolveODocument(formKey));
		
		Form<ODocument> form = new Form<ODocument>("form", getModel());
		IModel<List<OProperty>> propertiesModel = new LoadableDetachableModel<List<OProperty>>() {
			@Override
			protected List<OProperty> load() {
				return oClassIntrospector.listProperties(formDocumentModel.getObject().getSchemaClass(), IOClassIntrospector.DEFAULT_TAB, false);
			}
		};
		propertiesStructureTable = new OrienteerStructureTable<ODocument, OProperty>("properties", formDocumentModel, propertiesModel){

					@Override
					protected Component getValueComponent(String id,
							IModel<OProperty> rowModel) {
						return new ODocumentMetaPanel<Object>(id, getModeModel(), formDocumentModel, rowModel);
					}
		};
		form.add(propertiesStructureTable);
		add(form);
	}
	
	protected abstract FormKey obtainFormKey();
	
	protected abstract ODocument resolveODocument(FormKey formKey);
	
	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.tasks);
	}
	
	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("widget.form");
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		formDocumentModel.detach();
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

}
