package org.orienteer.core.component.widget.document;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.OFunctionExecuteCommand;
import org.orienteer.core.component.meta.OFunctionExecutionMetaPanel;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.Widget;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Widget to execute function
 */
@Widget(domain="document", id ="document-function", selector = "OFunction", order = 10, autoEnable=true)
public class ODocumentFunctionExecutionWidget extends AbstractModeAwareWidget<ODocument>{

	private OrienteerStructureTable<OFunctionExecutionModel, String> propertiesStructureTable;

    public ODocumentFunctionExecutionWidget(String id, IModel<ODocument> model,
                                            IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
        ODocument doc = getModel().getObject();
        final IModel<OFunctionExecutionModel> modelObj = Model.of(convertModel(doc));
		Form<OFunctionExecutionModel> form = new Form<OFunctionExecutionModel>("form", modelObj);

        IModel<List<String>> propertiesModel = new LoadableDetachableModel<List<String>>() {
            @Override
            protected List<String> load() {
                List<String> ret = new ArrayList<String>();
                ret.add(OFunctionExecutionModel.NAME);
                ret.add(OFunctionExecutionModel.PARAMETERS);
                ret.add(OFunctionExecutionModel.RESULT);
                return ret;
            }
        };

		propertiesStructureTable = new OrienteerStructureTable<OFunctionExecutionModel, String>("properties", modelObj, propertiesModel){

					@Override
					protected Component getValueComponent(String id,
							IModel<String> rowModel) {
						return new OFunctionExecutionMetaPanel<Object>(id, getModeModel(), modelObj, rowModel);
					}
		};

        propertiesStructureTable.addCommand(new OFunctionExecuteCommand(propertiesStructureTable));
		form.add(propertiesStructureTable);
		add(form);

	}

    private OFunctionExecutionModel convertModel(ODocument doc) {
        OFunctionExecutionModel model = new OFunctionExecutionModel(doc);
        model.setName((String) doc.field(OFunctionExecutionModel.NAME));
        List<String> parameters = doc.field(OFunctionExecutionModel.PARAMETERS);
        Map<String, String> params = new LinkedHashMap<>();
        for(String param : parameters) {
            params.put(param, "");
        }
        model.setParameters(params);
        return model;
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
		return new ResourceModel("document.execute");
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

}
