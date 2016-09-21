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
import org.apache.wicket.ajax.AjaxRequestTarget;
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
import ru.ydn.wicket.wicketorientdb.components.TransactionlessForm;
import ru.ydn.wicket.wicketorientdb.model.ListOPropertiesModel;
import ru.ydn.wicket.wicketorientdb.model.OClassCustomModel;
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

	private OrienteerStructureTable<OClass, String> structureTable;		

	public OClassHooksWidget(String id, final IModel<OClass> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		
		final List<String> functions = new ArrayList<String>(getDatabase().getMetadata().getFunctionLibrary().getFunctionNames());
		
		Form form = new TransactionlessForm<>("form");
		add(form);

		structureTable = new OrienteerStructureTable<OClass, String>("events", model, EVENTS_LIST) {

			@Override
			protected Component getValueComponent(String id, IModel<String> rowModel) {
				return new AbstractModeMetaPanel<OClass, DisplayMode, String, String>(id, getModeModel(), OClassHooksWidget.this.getModel(), rowModel) {

					//здесь задается ,какой компонент мы будем показывать в виде поля "значение"
					@Override
					protected Component resolveComponent(String id,
							DisplayMode mode, String critery) {
						if(DisplayMode.EDIT.equals(mode)) {
							return new DropDownChoice<String>(
									id,
									getValueModel(),
									functions
							).setNullValid(true);
						} else {
							return new Label(id, getValueModel());
						}
					}
					//видимо это модель,которую мы показываем в поле "имя".//getPropertyModel - проперти - это ИД поля в обьекте
					@Override
					protected IModel<String> newLabelModel() {
						return getPropertyModel();//simplenamingmodel
					}
					//здесь мы задаем модель, обрабатывающую получаемое значение
					@Override
					protected IModel<String> resolveValueModel() {
						return new OClassCustomModel(getEntityModel(),getPropertyModel());//createCustomModel(getEntityModel(), getPropertyModel());
					}
				};
				
			}
		};
		
		form.add(structureTable);
		structureTable.addCommand(new EditSchemaCommand<OClass>(structureTable, getModeModel()));
		structureTable.addCommand(new SaveSchemaCommand<OClass>(structureTable, getModeModel()));/*{
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				boolean isTransactionActive = getDatabase().getTransaction().isActive();
				if(isTransactionActive) getDatabase().commit();
				try {
					super.onSubmit(target, form);
				} finally {
					if(isTransactionActive) getDatabase().begin();
				}
			}
		});
		*/
/*
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
*/
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		/*
			if(((Form)get("form")).isSubmitted()) 
			{
				setModeObject(DisplayMode.VIEW);
			}
			*/
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
