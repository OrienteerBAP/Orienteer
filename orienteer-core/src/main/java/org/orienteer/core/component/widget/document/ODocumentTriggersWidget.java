package org.orienteer.core.component.widget.document;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.EditCommand;
import org.orienteer.core.component.command.SaveOTriggerCommand;
import org.orienteer.core.component.meta.OTriggerMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OTriggerMetaColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.model.OTriggerModel;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.model.AbstractJavaSortableDataProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Widget to add trigger to a document of OTriggered subclass
 */
@Widget(domain="document", id ="document-trigger", selector = "OTriggered", order = 10, autoEnable=true)
public class ODocumentTriggersWidget extends AbstractModeAwareWidget<ODocument>{

	private OrienteerDataTable<OTriggerModel, String> triggersTable;

    public ODocumentTriggersWidget(String id, IModel<ODocument> model,
                                   IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		Form<ODocument> form = new Form<ODocument>("form", getModel());
        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();

        List<IColumn<OTriggerModel, String>> pColumns = new ArrayList<IColumn<OTriggerModel,String>>();
        pColumns.add(new OTriggerMetaColumn(OTriggerModel.TRIGGER, modeModel));
        pColumns.add(new OTriggerMetaColumn(OTriggerModel.FUNCTION, modeModel));

        IModel<Collection<OTriggerModel>> triggers = Model.of(triggers(model.getObject()));
        ISortableDataProvider<OTriggerModel, String> pProvider = new AbstractJavaSortableDataProvider<OTriggerModel, String>(triggers) {
            @Override
            public IModel<OTriggerModel> model(final OTriggerModel oTriggerModel) {
               return Model.of(oTriggerModel);
            }
        };

        triggersTable = new OrienteerDataTable<OTriggerModel, String>("properties", pColumns, pProvider ,20);
		form.add(triggersTable);
        triggersTable.addCommand(new EditCommand<OTriggerModel>(triggersTable, modeModel));
        triggersTable.addCommand(new SaveOTriggerCommand(triggersTable, modeModel));

        add(form);

	}

    private List<OTriggerModel> triggers(ODocument document) {
        List<OTriggerModel> triggerModels = new ArrayList<>();
        List<String> oTriggerList = OTriggerMetaPanel.OTRIGGER_LIST;
        for(String triggerName : oTriggerList) {
            String functionName =  document.field(triggerName);
            if(functionName != null) {
                triggerModels.add(new OTriggerModel(document, triggerName, functionName));
            } else {
                triggerModels.add(new OTriggerModel(document, triggerName, null));
            }
        }
        return triggerModels;
    }

	@Override
	protected void onConfigure() {
		super.onConfigure();
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.bars);
	}

	@Override
	protected IModel<String> getTitleModel() {
		return new ResourceModel("document.triggers");
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

}
