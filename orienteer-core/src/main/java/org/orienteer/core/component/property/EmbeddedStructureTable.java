package org.orienteer.core.component.property;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.meta.ODocumentMetaPanel;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.service.IOClassIntrospector;

import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link OrienteerStructureTable} to display embedded properties
 */
public class EmbeddedStructureTable extends OrienteerStructureTable<ODocument, OProperty> {
	
	
	private static class EmbeddedDocumentDynamicValueModel extends DynamicPropertyValueModel<ODocument>
	{

		public EmbeddedDocumentDynamicValueModel(IModel<ODocument> docModel,
				IModel<OProperty> propertyModel) {
			super(docModel, propertyModel);
		}
		
		@Override
		protected ODocument load() {
			ODocument ret =  super.load();
			if(ret==null)
			{
				OProperty property = propertyModel.getObject();
				OClass embeddedClass = property.getLinkedClass();
				ret = new ODocument(embeddedClass);
				docModel.getObject().field(property.getName(), ret);
			}
			return ret;
		}
		
	}
	
	private IModel<DisplayMode> displayModeModel;
	
	public EmbeddedStructureTable(String id, IModel<ODocument> mainDocumentModel, final IModel<OProperty> propertyModel, IModel<DisplayMode> displayModeModel) {
		super(id, new EmbeddedDocumentDynamicValueModel(mainDocumentModel, propertyModel), 
							new LoadableDetachableModel<List<OProperty>>() {
								@Override
								protected List<OProperty> load() {
									IOClassIntrospector oClassIntrospector = OrienteerWebApplication.get().getOClassIntrospector();
									OClass linkedClass = propertyModel.getObject().getLinkedClass();
									return oClassIntrospector.listProperties(linkedClass, new Predicate<OProperty>() {
										@Override
										public boolean apply(OProperty input) {
											return !((Boolean)CustomAttribute.HIDDEN.getValue(input));
										}
									});
								}
					});
		this.displayModeModel = displayModeModel;
	}

	@Override
	protected Component getValueComponent(String id,
			IModel<OProperty> rowModel) {
		return new ODocumentMetaPanel<Object>(id, displayModeModel, getModel(), rowModel);
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		displayModeModel.detach();
	}

}
