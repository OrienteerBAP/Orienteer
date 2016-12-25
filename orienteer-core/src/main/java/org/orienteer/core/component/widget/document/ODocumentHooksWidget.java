/**
 * 
 */
package org.orienteer.core.component.widget.document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.ODocumentPageLink;
import org.orienteer.core.component.command.EditODocumentCommand;
import org.orienteer.core.component.command.EditSchemaCommand;
import org.orienteer.core.component.command.SaveODocumentCommand;
import org.orienteer.core.component.command.SaveSchemaCommand;
import org.orienteer.core.component.meta.AbstractMetaPanel;
import org.orienteer.core.component.meta.AbstractModeMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.widget.oclass.OClassHooksWidget;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.util.ODocumentChoiceRenderer;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.Widget;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.components.TransactionlessForm;
import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.OClassCustomModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

/**
 * @author Asm
 * Widget for document-linked hooks
 *
 */

@Widget(domain="document",selector="OTriggered", id=ODocumentHooksWidget.WIDGET_TYPE_ID, order=30, autoEnable=false)
public class ODocumentHooksWidget extends AbstractModeAwareWidget<ODocument> {
	public static final String WIDGET_TYPE_ID = "documentHooks";
	
	private OrienteerStructureTable<ODocument, OProperty> structureTable;

    @Inject
    private IOClassIntrospector oClassIntrospector;
    
	public static final List<String> EVENTS_LIST = new ArrayList<String>();
	static
	{
		EVENTS_LIST.add("onBeforeRead");
		EVENTS_LIST.add("onAfterRead");
		EVENTS_LIST.add("onBeforeUpdate");
		EVENTS_LIST.add("onAfterUpdate");
		EVENTS_LIST.add("onBeforeDelete");
		EVENTS_LIST.add("onAfterDelete");
	}
	
	public ODocumentHooksWidget(String id, final IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		
		final OQueryModel<ODocument> functions = new OQueryModel<ODocument>("SELECT FROM OFunction");

		Form<?> form = new TransactionlessForm<>("form");
		add(form);

        IModel<List<OProperty>> propertiesModel = new LoadableDetachableModel<List<OProperty>>() {
			@Override
			protected List<OProperty> load() {
				ODocument doc = model.getObject();
				Set<String> fieldNames = new HashSet<String>(Arrays.asList(doc.fieldNames()));
				Set<String> propertiesNames = doc.getSchemaClass().propertiesMap().keySet();
				fieldNames.removeAll(propertiesNames);
				List<OProperty> ret = new ArrayList<OProperty>(EVENTS_LIST.size());
				for (String field : EVENTS_LIST) {
					ret.add(oClassIntrospector.virtualizeField(doc, field));
				}
				return ret;
			}
		};
		
		structureTable = new OrienteerStructureTable<ODocument, OProperty>("events", model, propertiesModel){
			@Override
			protected Component getValueComponent(String id, IModel<OProperty> rowModel) {
				return new AbstractModeMetaPanel<ODocument, DisplayMode, OProperty,ODocument >(id, getModeModel(), ODocumentHooksWidget.this.getModel(), rowModel) {

					@Override
					protected Component resolveComponent(String id, DisplayMode mode, OProperty critery) {
						if(DisplayMode.EDIT.equals(mode)) {
							return new DropDownChoice<ODocument>(
									id,
									getValueModel(),
									functions,
									new ODocumentChoiceRenderer()
							).setNullValid(true);
						} else {
							return new ODocumentPageLink(id, getValueModel()).setDocumentNameAsBody(true);  
						}
					}

					@Override
					protected IModel<String> newLabelModel() {
						return new SimpleNamingModel<String>("widget.document.hooks."+ getPropertyObject().getName());
					}

					@Override
					protected IModel<ODocument> resolveValueModel() {
						return new ODocumentPropertyModel<>(getEntityModel(), getPropertyObject().getName());
					}
				};
			}
		}; 

		form.add(structureTable);
		structureTable.addCommand(new EditODocumentCommand(structureTable, getModeModel()));
		structureTable.addCommand(new SaveODocumentCommand(structureTable, getModeModel()));		
	}

	@Override
	protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.list);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
        return new ResourceModel("widget.document.hooks");
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}	
}
