package org.orienteer.core.component.property;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.meta.ODocumentMetaPanel;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.service.IOClassIntrospector;

import com.google.common.base.Predicate;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;

/**
 * Panel to show/edit some embedded document 
 */
public class EmbeddedDocumentPanel extends FormComponentPanel<ODocument> {
	
	private IModel<OClass> classModel;
	private IModel<DisplayMode> modeModel;
	private IModel<ODocument> inputDocumentModel;

	public EmbeddedDocumentPanel(String id, IModel<ODocument> model, IModel<OClass> classModel, IModel<DisplayMode> modeModel) {
		super(id, model);
		this.classModel = classModel;
		this.modeModel = modeModel;
		this.inputDocumentModel = new ODocumentModel();
		IModel<List<OProperty>> propertiesModel = new LoadableDetachableModel<List<OProperty>>() {
			@Override
			protected List<OProperty> load() {
				IOClassIntrospector oClassIntrospector = OrienteerWebApplication.get().getOClassIntrospector();
				OClass linkedClass = EmbeddedDocumentPanel.this.classModel.getObject();
				return oClassIntrospector.listProperties(linkedClass, new Predicate<OProperty>() {
					@Override
					public boolean apply(OProperty input) {
						return !((Boolean)CustomAttribute.HIDDEN.getValue(input));
					}
				});
			}
		};
		add(new OrienteerStructureTable<ODocument, OProperty>("table", inputDocumentModel, propertiesModel) {

			@Override
			protected Component getValueComponent(String id, IModel rowModel) {
				return new ODocumentMetaPanel<>(id, EmbeddedDocumentPanel.this.modeModel, getModel(), rowModel);
			}
		});
	}
	
	@Override
	protected void onBeforeRender() {
		ODocument currentDocument = getModelObject();
		if(currentDocument==null) currentDocument = new ODocument(classModel.getObject());
		inputDocumentModel.setObject(currentDocument); 
		super.onBeforeRender();
	}
	
	@Override
	public void convertInput() {
		setConvertedInput(inputDocumentModel.getObject());
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		if(classModel!=null) classModel.detach();
		if(modeModel!=null) modeModel.detach();
		inputDocumentModel.detach();
	}
	
}
