package org.orienteer.core.component.widget.document;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.EditODocumentCommand;
import org.orienteer.core.component.command.SaveODocumentCommand;
import org.orienteer.core.component.meta.ODocumentMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.visualizer.DefaultVisualizer;
import org.orienteer.core.component.visualizer.IVisualizer;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.Widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Widget to show unregistered parameters for a document on particular tab
 */
@Widget(domain="document", tab="parameters", id = ODocumentNonRegisteredPropertiesWidget.WIDGET_TYPE_ID, order=20, autoEnable=true)
public class ODocumentNonRegisteredPropertiesWidget extends AbstractModeAwareWidget<ODocument> {

    public static final String WIDGET_TYPE_ID = "nonregistered";

    @Inject
    private IOClassIntrospector oClassIntrospector;


    private OrienteerStructureTable<ODocument, OProperty> propertiesStructureTable;
    private SaveODocumentCommand saveODocumentCommand;

    public ODocumentNonRegisteredPropertiesWidget(String id, final IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);

        Form<ODocument> form = new Form<ODocument>("form", getModel());
        IModel<List<? extends OProperty>> propertiesModel = new LoadableDetachableModel<List<? extends OProperty>>() {
			@Override
			protected List<? extends OProperty> load() {
				ODocument doc = model.getObject();
				Set<String> fieldNames = new HashSet<String>(Arrays.asList(doc.fieldNames()));
				Set<String> propertiesNames = doc.getSchemaClass().propertiesMap().keySet();
				fieldNames.removeAll(propertiesNames);
				List<OProperty> ret = new ArrayList<OProperty>(fieldNames.size());
				for (String field : fieldNames) {
					ret.add(oClassIntrospector.virtualizeField(doc, field));
				}
				//Lets arrange it by field name
				Collections.sort(ret);
				return ret;
			}
		};

        propertiesStructureTable = new OrienteerStructureTable<ODocument, OProperty>("properties", getModel(), propertiesModel){

			@Override
			protected Component getValueComponent(String id,
					IModel<OProperty> rowModel) {
				return new ODocumentMetaPanel<Object>(id, getModeModel(), ODocumentNonRegisteredPropertiesWidget.this.getModel(), rowModel);
			}
		};
		form.add(propertiesStructureTable);
		add(form);
    }
    
    @Override
	protected void onInitialize() {
		super.onInitialize();
		propertiesStructureTable.addCommand(new EditODocumentCommand(propertiesStructureTable, getModeModel()));
		propertiesStructureTable.addCommand(saveODocumentCommand = new SaveODocumentCommand(propertiesStructureTable, getModeModel()));
	}
    
    @Override
	protected void onConfigure() {
		super.onConfigure();
		IModel<List<? extends OProperty>> propertiesModel = propertiesStructureTable.getCriteriesModel();
		List<? extends OProperty> properties = propertiesModel.getObject();
		setVisible(properties!=null && !properties.isEmpty());
		if(DisplayMode.EDIT.equals(getModeObject()))
		{
			saveODocumentCommand.configure();
			if(!saveODocumentCommand.determineVisibility())
			{
				setModeObject(DisplayMode.VIEW);
			}
		}
	}

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.bars);
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel("widget.document.unregistered.properties");
    }
    
    @Override
	protected String getWidgetStyleClass() {
		return "strict";
	}
}
