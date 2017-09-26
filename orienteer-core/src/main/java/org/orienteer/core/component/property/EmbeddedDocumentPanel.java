package org.orienteer.core.component.property;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.behavior.RefreshMetaContextOnChangeBehaviour;
import org.orienteer.core.component.meta.ODocumentMetaPanel;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.model.SubClassesModel;
import org.orienteer.core.service.IOClassIntrospector;

import com.google.common.base.Predicate;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.utils.OClassChoiceRenderer;

/**
 * Panel to show/edit some embedded document 
 */
public class EmbeddedDocumentPanel extends FormComponentPanel<ODocument> {
	
	private IModel<OClass> rootClassModel;
	private IModel<List<OClass>> subClassesModel;
	private IModel<DisplayMode> modeModel;
	private IModel<ODocument> inputDocumentModel;
	
	private DropDownChoice<OClass> embeddedClassChoice;
	private OrienteerStructureTable<ODocument, OProperty> propertyTable;

	public EmbeddedDocumentPanel(String id, IModel<ODocument> model, IModel<OClass> rootClassModel, IModel<DisplayMode> modeModel) {
		super(id, model);
		this.rootClassModel = rootClassModel;
		this.subClassesModel = new SubClassesModel(rootClassModel, true, false);
		this.modeModel = modeModel;
		this.inputDocumentModel = new ODocumentModel(prepareEmbeddedDocument());
		
		embeddedClassChoice = new DropDownChoice<>("docClass", 
				new OClassModel(new PropertyModel<String>(inputDocumentModel, "@className")), 
				subClassesModel,
				OClassChoiceRenderer.INSTANCE);
		OClass rootClass = rootClassModel.getObject();
		embeddedClassChoice.setVisibilityAllowed(rootClass!=null && !rootClass.getSubclasses().isEmpty());
		embeddedClassChoice.add(new AjaxFormSubmitBehavior("change"){
			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				embeddedClassChoice.convertInput();
				if(target!=null) target.add(propertyTable);
			}
			
			@Override
			public boolean getDefaultProcessing() {
				return false;
			}
		});
		add(embeddedClassChoice);
		IModel<List<OProperty>> propertiesModel = new LoadableDetachableModel<List<OProperty>>() {
			@SuppressWarnings("unchecked")
			@Override
			protected List<OProperty> load() {
				IOClassIntrospector oClassIntrospector = OrienteerWebApplication.get().getOClassIntrospector();
				OClass classFormToShow = embeddedClassChoice.getConvertedInput();
				if(classFormToShow==null) classFormToShow = embeddedClassChoice.getModelObject();
				return oClassIntrospector.listProperties(classFormToShow, new Predicate<OProperty>() {
					@Override
					public boolean apply(OProperty input) {
						return !((Boolean)CustomAttribute.HIDDEN.getValue(input));
					}
				});
			}
		};
		propertyTable = new OrienteerStructureTable<ODocument, OProperty>("table", inputDocumentModel, propertiesModel) {

			@Override
			protected Component getValueComponent(String id, IModel rowModel) {
				return new ODocumentMetaPanel<>(id, EmbeddedDocumentPanel.this.modeModel, getModel(), rowModel);
			}
		};
		propertyTable.setOutputMarkupId(true);
		add(propertyTable);
	}
	
	private ODocument prepareEmbeddedDocument() {
		OClass embeddedClass = embeddedClassChoice!=null?embeddedClassChoice.getModelObject():null;
		if(embeddedClass==null) embeddedClass = rootClassModel.getObject();
		return new ODocument(embeddedClass);
	}
	
	@Override
	protected void onBeforeRender() {
		ODocument currentDocument = getModelObject();
		inputDocumentModel.setObject(currentDocument!=null?currentDocument:prepareEmbeddedDocument()); 
		super.onBeforeRender();
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		embeddedClassChoice.setVisible(DisplayMode.EDIT.equals(modeModel.getObject()));
	}
	
	@Override
	public void convertInput() {
		setConvertedInput(inputDocumentModel.getObject());
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		if(rootClassModel!=null) rootClassModel.detach();
		if(subClassesModel!=null) subClassesModel.detach();
		if(modeModel!=null) modeModel.detach();
		inputDocumentModel.detach();
	}
	
}
