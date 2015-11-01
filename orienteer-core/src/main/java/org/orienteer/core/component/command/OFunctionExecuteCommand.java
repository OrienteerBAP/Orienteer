package org.orienteer.core.component.command;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.function.OFunction;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.widget.document.OFunctionExecutionModel;
import org.orienteer.core.web.ODocumentPage;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link Command} for execution {@link OFunction}
 */
public class OFunctionExecuteCommand extends AjaxFormCommand<OFunctionExecutionModel> implements ISecuredComponent {

    public OFunctionExecuteCommand(OrienteerStructureTable<OFunctionExecutionModel, String> propertiesStructureTable) {
        super(new ResourceModel("command.execute"), propertiesStructureTable);
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        OFunctionExecutionModel model = getModel().getObject();
        OFunction oFunction = new OFunction(model.getDocument());
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.putAll(model.getParameters());

        Object resultValue = oFunction.execute(paramMap);
        model.setResult(resultValue.toString());
        target.add(this);
        super.onClick(target);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        OFunctionExecutionModel object = getModel().getObject();
        this.setEnabled(object != null && object.getName()!=null);
    }

    @Override
    public RequiredOrientResource[] getRequiredResources() {
        return new RequiredOrientResource[0];
    }
}
