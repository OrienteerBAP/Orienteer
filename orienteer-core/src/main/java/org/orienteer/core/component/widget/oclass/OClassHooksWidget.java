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
	
    private ListView<String> propertiesListView;
    //private OrienteerStructureTable<OClass, String> propertiesStructureTable;
    private SaveSchemaCommand saveODocumentCommand;
    
    @Inject
    private IOClassIntrospector oClassIntrospector;
	private OrienteerStructureTable<ArrayList<String>, String> structureTable;

	public static final List<String> EVENTS_LIST = new ArrayList<String>();
	static
	{
		//EVENTS_LIST.add("");
		EVENTS_LIST.add("onBeforeCreate");
		EVENTS_LIST.add("onAfterCreate");
		EVENTS_LIST.add("onBeforeRead");
		EVENTS_LIST.add("onAfterRead");
		EVENTS_LIST.add("onBeforeUpdate");
		EVENTS_LIST.add("onAfterUpdate");
		EVENTS_LIST.add("onBeforeDelete");
		EVENTS_LIST.add("onAfterDelete");
	}		
	
	public class Events implements Serializable{
		protected String onBeforeCreate;
		protected String onAfterCreate;
		protected String onBeforeRead;
		protected String onAfterRead;
		protected String onBeforeUpdate;
		protected String onAfterUpdate;
		protected String onBeforeDelete;
		protected String onAfterDelete;
		
	}
	
//getModeModel
	public OClassHooksWidget(String id, final IModel<OClass> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		/*
		final List<String> events = new ArrayList<String>();
		events.add("onBeforeCreate");
		events.add("onAfterCreate");
		events.add("onBeforeRead");
		events.add("onAfterRead");
		events.add("onBeforeUpdate");
		events.add("onAfterUpdate");
		events.add("onBeforeDelete");
		events.add("onAfterDelete");
		*/
		/*
		HashMap<String, String> eventsMap = new HashMap<String, String>();
		for(int i=0; i <events.size();i++){
			eventsMap.put(events.get(i), "");
		}
		*/

		/*
		IModel<List<OProperty>> propertiesModel = new LoadableDetachableModel<List<OProperty>>() {
			@Override
			protected List<OProperty> load() {
				OClass obj = model.getObject();

				List<OProperty> ret = new ArrayList<OProperty>();//fieldNames.size());
//				ret.add(oClassIntrospector.virtualizeField(obj.getDocument(), prop.getName()));

				//Set<String> fieldNames = new HashSet<String>(Arrays.asList(obj.properties()));
				//Set<String> propertiesNames = doc.getSchemaClass().propertiesMap().keySet();
				//fieldNames.removeAll(propertiesNames);

				for (String event : events) {
					ret.add(oClassIntrospector.virtualizeField(((ODocumentWrapper) obj).getDocument(), event));
				}
				//Lets arrange it by field name
				//Collections.sort(ret);
				return ret;
			}
		};
*/
		//final List<String> events2db = new ArrayList<String>();
		//for (String val : EVENTS_LIST) {
		//	events2db.add("customFields."+val+" as "+val);
		//}

		//final OQueryModel<ODocument> q= new OQueryModel<ODocument>("select "+Strings.join(",", events2db)+" from (SELECT expand(classes) from metadata:schema) where name=\""+model.getObject().getName()+"\"");
		
		final ListView<String> eventsView = new ListView<String>("events", EVENTS_LIST) {
			@Override
			protected void populateItem(ListItem<String> item) {
				item.add(new Label("description", item.getModel()));
				//item.add(new Label("description2", Model.of(Strings.toString(q.getObject().get(0).field(item.getModelObject().toString())))));
				//model.getObject().getCustom(item.getModelObject())
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
				//String setq = "ALTER CLASS \""+model.getObject().getName()+"\" CUSTOM ";
				List<String> retarr = new ArrayList<String>();
				
				for (Component val : eventsView) {
					String eventVal = ((ListItem<String>)val).get("input").getDefaultModelObjectAsString();
					String eventName = ((ListItem<String>)val).get("description").getDefaultModelObjectAsString();
					//retarr.add(((ListItem<String>)val).get("description").getDefaultModelObjectAsString()+"=\""+((ListItem<String>)val).get("input").getDefaultModelObjectAsString()+"\"");
			    	ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
			    	db.commit();
			    	model.getObject().setCustom(eventName, eventVal.isEmpty()?null:eventVal);
			    	

//					String retq;
					
					
					//if (Strings.isEmpty(retVal)){
					//	retq="ALTER CLASS "+model.getObject().getName()+" CUSTOM "+((ListItem<String>)val).get("description").getDefaultModelObjectAsString()+"=\"\"";
						
					//}else{
					//	retq="ALTER CLASS "+model.getObject().getName()+" CUSTOM "+((ListItem<String>)val).get("description").getDefaultModelObjectAsString()+"="+((ListItem<String>)val).get("input").getDefaultModelObjectAsString()+"";
					//}
					
			    	//OCommandSQL query = new OCommandSQL(retq);
			    	//db.command(query);
			    	//db.commit();
					//debug(retq);
					//events2db.add("customFields."+val+" as "+val);
				}
				//new OQueryModel<String>(retq);
//				retq.
				/*
				for(Iterator<String> i = someList.iterator(); i.hasNext(); ) {
				    String item = i.next();
				    System.out.println(item);
				}
				*/
				super.onSubmit();
			}
		};
		
		form.add(eventsView);
		/* //совершенно не понимаю, что ему тут надо 
		Events ev = new Events(); 
		form.add(new OrienteerStructureTable<Events,String>("events",Model.of(ev), EVENTS_LIST){
			@Override
			protected Component getValueComponent(String id, IModel<String> rowModel) {
				return new AbstractModeMetaPanel<Events, DisplayMode, String, String>(id, getModeModel(), OClassHooksWidget.this.getModel(), rowModel) {

					@Override
					protected Component resolveComponent(String id,	DisplayMode mode, String critery) {
						return null;
						
						//if(DisplayMode.EDIT.equals(mode)) {
						//	return new TextArea<String>(id, getValueModel());
						//} else {
						//	return new MultiLineLabel(id, getValueModel());
						//}
						
					}

					@Override
					protected IModel<String> newLabelModel() {
						return null;//getPropertyModel();
					}

					@Override
					protected IModel<String> resolveValueModel() {
						return null;//createCustomModel(getEntityModel(), getPropertyModel());
					}
				};
			}
		}
		);
		*/
		/*
		structureTable = new OrienteerStructureTable<ArrayList<String>, String>("table", Model.of(events), new PropertyModel<List<String>>(this, "custom")) {

			@Override
			protected Component getValueComponent(String id,
					IModel<String> rowModel) {
				return new AbstractModeMetaPanel<T, DisplayMode, String, String>(id, getModeModel(), AbstractSchemaCustomPropertiesWidget.this.getModel(), rowModel) {

					@Override
					protected Component resolveComponent(String id,
							DisplayMode mode, String critery) {
						if(DisplayMode.EDIT.equals(mode)) {
							return new TextArea<String>(id, getValueModel());
						} else {
							return new MultiLineLabel(id, getValueModel());
						}
					}

					@Override
					protected IModel<String> newLabelModel() {
						return getPropertyModel();
					}

					@Override
					protected IModel<String> resolveValueModel() {
						return createCustomModel(getEntityModel(), getPropertyModel());
					}
				};
				
			}
		};
		*/
		
		//for (String val : events) {
		//	form.add(new Label(val, new OQueryModel<ODocument>("select "+Strings.join(",", events2db)+" from (SELECT expand(classes) from metadata:schema) where name=\""+model.getObject().getName()+"\"")));
		//}
	
		/*
		form.add(new DropDownChoice<String>(
				"select",
				//Model.of(""),
				 new ArrayList<String>(getDatabase().getMetadata().getFunctionLibrary().getFunctionNames())
		));
*/
		/*
		form.add( propertiesListView = new ListView<String>("properties", events) {
			@Override
			protected void populateItem(ListItem<String> item) {
				item.add(new Label("description", item.getModel()));
				item.add(new TextField<String>("input", item.getModel(), String.class));
			}
		});
		*/
		/*
		propertiesStructureTable  = new OrienteerStructureTable<OClass,String>("properties", getModel(), events) {
			@Override
			protected Component getValueComponent(String id, final IModel<String> rowModel) {
				return new TextField<String>(id, rowModel, String.class);
				//return new Label(id,rowModel);
				//return new OClassMetaPanel<Object>(id, getModeModel(), OClassHooksWidget.this.getModel(), rowModel);
			}
			
		};
		*/
		//form.add(propertiesStructureTable);
		//getDatabase().getMetadata().getSchema().
		add(form);
		//add(new Label("text", "^^^^^^"+(String)getDatabase().getMetadata().getFunctionLibrary().getFunction("func1").execute()));
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		//propertiesStructureTable.addCommand(new EditSchemaCommand(propertiesStructureTable, getModeModel()));
		
		//propertiesStructureTable.addCommand(saveODocumentCommand = new AbstractSaveCommand(propertiesStructureTable, getModeModel()));
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		//if(DisplayMode.EDIT.equals(getModeModel().getObject())){

			//saveODocumentCommand.configure();
			if(((Form)get("form")).isSubmitted()) 
			{
				setModeObject(DisplayMode.VIEW);
			}

		//}
	}
	

	@Override
	protected FAIcon newIcon(String id) {
		// TODO Auto-generated method stub
        return new FAIcon(id, FAIconType.list);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
        return new ResourceModel("class.hooks");
	}

}
