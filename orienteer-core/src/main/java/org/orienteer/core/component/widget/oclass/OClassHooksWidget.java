/**
 * 
 */
package org.orienteer.core.component.widget.oclass;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OClassTrigger;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.ODocumentPageLink;
import org.orienteer.core.component.command.EditSchemaCommand;
import org.orienteer.core.component.command.SaveSchemaCommand;
import org.orienteer.core.component.meta.AbstractModeMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.util.ODocumentChoiceRenderer;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.components.TransactionlessForm;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Widget for class-linked hooks
 * Instead storing function names, we store their rid-s in trigger fields
 */
@Widget(domain="class", tab="configuration",selector=OClassTrigger.CLASSNAME, id = OClassHooksWidget.WIDGET_TYPE_ID, order=20, autoEnable=true)
public class OClassHooksWidget extends AbstractModeAwareWidget<OClass> {

	public static final String WIDGET_TYPE_ID = "classHooks";
	
	public static final List<String> EVENTS_LIST = new ArrayList<String>();
	static
	{
		EVENTS_LIST.add("onBeforeCreate");
		EVENTS_LIST.add("onAfterCreate");
		EVENTS_LIST.add("onBeforeRead");
		EVENTS_LIST.add("onAfterRead");
		EVENTS_LIST.add("onBeforeUpdate");
		EVENTS_LIST.add("onAfterUpdate");
		EVENTS_LIST.add("onBeforeDelete");
		EVENTS_LIST.add("onAfterDelete");
	}

	private OrienteerStructureTable<OClass, String> structureTable;		

	public OClassHooksWidget(String id, final IModel<OClass> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		final OQueryModel<ODocument> functions = new OQueryModel<ODocument>("SELECT FROM OFunction");
		
		
		Form form = new TransactionlessForm<>("form");
		add(form);

		structureTable = new OrienteerStructureTable<OClass, String>("events", model, EVENTS_LIST) {
			@Override
			protected Component getValueComponent(String id, IModel<String> rowModel) {
				return new AbstractModeMetaPanel<OClass, DisplayMode, String, ODocument>(id, getModeModel(), OClassHooksWidget.this.getModel(), rowModel) {
					@Override
					protected Component resolveComponent(String id,	DisplayMode mode, String critery) {
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
						return new SimpleNamingModel<String>("class.hooks."+ getPropertyObject());
					}
					@Override
					protected IModel<ODocument> resolveValueModel() {
						
						return new LoadableDetachableModel<ODocument>() {
							@Override
							protected ODocument load() {
								String idStr = getEntityObject().getCustom(getPropertyObject());
								if (Strings.isEmpty(idStr)) return null;
								else if (ORecordId.isA(idStr)){
									return new ORecordId(idStr).getRecord();
								}else{
							    	ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
									return (ODocument) db.query("select from OFunction where name = ?", idStr)
											.elementStream()
											.findFirst()
											.orElse(null);
								}
							}
							
							@Override
							public void setObject(ODocument value) {
								getEntityObject().setCustom(getPropertyObject(), (value!=null?value.getIdentity().toString():null));
								super.setObject(value);
							}
						};
					}
				};
			}
		};
		
		form.add(structureTable);
		structureTable.addCommand(new EditSchemaCommand<OClass>(structureTable, getModeModel()));
		structureTable.addCommand(new SaveSchemaCommand<OClass>(structureTable, getModeModel()));

	}
	
	@Override
	protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.list);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
        return new ResourceModel("class.hooks");
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

}
