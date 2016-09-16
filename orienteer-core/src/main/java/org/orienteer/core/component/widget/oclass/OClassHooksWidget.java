/**
 * 
 */
package org.orienteer.core.component.widget.oclass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.EditODocumentCommand;
import org.orienteer.core.component.command.EditSchemaCommand;
import org.orienteer.core.component.command.SaveODocumentCommand;
import org.orienteer.core.component.command.SaveSchemaCommand;
import org.orienteer.core.component.meta.OClassMetaPanel;
import org.orienteer.core.component.meta.ODocumentMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.widget.document.ODocumentNonRegisteredPropertiesWidget;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.Widget;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.function.OFunction;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClassImpl;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

import ru.ydn.wicket.wicketorientdb.model.ListOPropertiesModel;

/**
 * @author Asm
 * Widget for class-linked hooks
 */
/*
 * приделать кнопочку редактирования
 * 
 * сделать разделение на редактирование и просмотр
 * 
 * сделать обработку заглушечных полей(эт поначалу они заглушечные ж ,а потооом...)
 * 
 * приделать список ListOFunctionsModel или как -то так ,наподобие ListOPropertiesModel
 * 
 * по-хорошему ,надо бы прихерачить отдельный селектор для функций.
 * 
 * */
@Widget(domain="class", tab="configuration",selector="OTriggered", id = OClassHooksWidget.WIDGET_TYPE_ID, order=20, autoEnable=true)
public class OClassHooksWidget extends AbstractModeAwareWidget<OClass> {

	public static final String WIDGET_TYPE_ID = "classHooks";
	
    private OrienteerStructureTable<OClass, String> propertiesStructureTable;
    private SaveSchemaCommand saveODocumentCommand;
    
    @Inject
    private IOClassIntrospector oClassIntrospector;

	
//getModeModel
	public OClassHooksWidget(String id, final IModel<OClass> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		
		final List<String> events = new ArrayList<String>();
		events.add("onBeforeCreate");
		events.add("onAfterCreate");
		events.add("onBeforeRead");
		events.add("onAfterRead");
		events.add("onBeforeUpdate");
		events.add("onAfterUpdate");
		events.add("onBeforeDelete");
		events.add("onAfterDelete");

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

		Form form = new Form("form");
		/*
		form.add(new DropDownChoice<String>(
				"select",
				//Model.of(""),
				 new ArrayList<String>(getDatabase().getMetadata().getFunctionLibrary().getFunctionNames())
		));
*/
		/*
		propertiesStructureTable = new OrienteerStructureTable<OClassImpl, OProperty>("properties", getModel(), propertiesModel){

			@Override
			protected Component getValueComponent(String id,IModel<OProperty> rowModel) {
				return new Label(id,rowModel.getObject().getName());
				//return new TextField<String>(id, Model.of(""), String.class);
				//return new TextField<OProperty>(id, rowModel, OProperty.class);
				//return new OClassMetaPanel<Object>(id, getModeModel(), OClassHooksWidget.this.getModel(), Model.of(rowModel.getObject().getName()));
				//return new OClassMetaPanel<Object>(id, getModeModel(), OClassHooksWidget.this.getModel(), rowModel);
			}
		};
		*/
		
		propertiesStructureTable  = new OrienteerStructureTable<OClass,String>("properties", getModel(), events) {
			@Override
			protected Component getValueComponent(String id, final IModel<String> rowModel) {
				//return new Label(id,rowModel);
				return new OClassMetaPanel<Object>(id, getModeModel(), OClassHooksWidget.this.getModel(), rowModel);
			}
			
		};
		
		form.add(propertiesStructureTable);
		add(form);
		add(new Label("text", "^^^^^^"+(String)getDatabase().getMetadata().getFunctionLibrary().getFunction("func1").execute()));
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		propertiesStructureTable.addCommand(new EditSchemaCommand(propertiesStructureTable, getModeModel()));
		
		propertiesStructureTable.addCommand(saveODocumentCommand = new SaveSchemaCommand(propertiesStructureTable, getModeModel()));
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
	
	protected Component getDefaultView(){
		return getDisplayView();
	}
	
	protected Component getEditView(){

		Form form = new Form("form");
		form.add(new DropDownChoice<String>(
				"select",
				//Model.of(""),
				 new ArrayList<String>(getDatabase().getMetadata().getFunctionLibrary().getFunctionNames())
		));

		form.add(propertiesStructureTable);

		Fragment fragment = new  Fragment ("body", "editView", this);
		fragment.add(form);
		return fragment;
	}
	
	protected Component getDisplayView(){
		
		Fragment fragment = new  Fragment ("body", "displayView", this);
		fragment.add(new Label("text", "^^^^^^"+(String)getDatabase().getMetadata().getFunctionLibrary().getFunction("func1").execute()));
		return fragment;
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
