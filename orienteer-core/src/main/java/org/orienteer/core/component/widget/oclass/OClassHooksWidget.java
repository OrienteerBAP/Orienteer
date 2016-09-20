/**
 * 
 */
package org.orienteer.core.component.widget.oclass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.CustomAttributes;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.EditODocumentCommand;
import org.orienteer.core.component.command.EditSchemaCommand;
import org.orienteer.core.component.command.SaveODocumentCommand;
import org.orienteer.core.component.command.SaveSchemaCommand;
import org.orienteer.core.component.meta.AbstractModeMetaPanel;
import org.orienteer.core.component.meta.OClassMetaPanel;
import org.orienteer.core.component.meta.ODocumentMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.structuretable.StructureTable;
import org.orienteer.core.component.widget.AbstractSchemaCustomPropertiesWidget;
import org.orienteer.core.component.widget.document.ODocumentNonRegisteredPropertiesWidget;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.Widget;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.function.OFunction;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClassImpl;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.ListOPropertiesModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper;

/**
 * @author Asm
 * Widget for class-linked hooks
 */
@Widget(domain="class", tab="configuration",selector="OTriggered", id = OClassHooksWidget.WIDGET_TYPE_ID, order=20, autoEnable=true)
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

	public OClassHooksWidget(String id, final IModel<OClass> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);

		final ListView<String> eventsView = new ListView<String>("events", EVENTS_LIST) {
			@Override
			protected void populateItem(ListItem<String> item) {
				item.add(new Label("description", item.getModel()));
				item.add(new DropDownChoice<String>(
						"input",
						Model.of(model.getObject().getCustom(item.getModelObject())),
						 new ArrayList<String>(getDatabase().getMetadata().getFunctionLibrary().getFunctionNames())
				).setNullValid(true));
			}
		};
		Form form = new Form("form"){
			@Override
			protected void onSubmit() {
				for (Component val : eventsView) {
					String eventVal = ((ListItem<String>)val).get("input").getDefaultModelObjectAsString();
					String eventName = ((ListItem<String>)val).get("description").getDefaultModelObjectAsString();
			    	ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
			    	db.commit();
			    	model.getObject().setCustom(eventName, eventVal.isEmpty()?null:eventVal);
				}
				super.onSubmit();
			}
		};
		
		form.add(eventsView);
		add(form);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
			if(((Form)get("form")).isSubmitted()) 
			{
				setModeObject(DisplayMode.VIEW);
			}
	}
	

	@Override
	protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.list);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
        return new ResourceModel("class.hooks");
	}

}
