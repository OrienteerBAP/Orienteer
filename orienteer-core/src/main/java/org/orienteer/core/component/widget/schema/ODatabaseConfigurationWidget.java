package org.orienteer.core.component.widget.schema;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.EditSchemaCommand;
import org.orienteer.core.component.command.SaveSchemaCommand;
import org.orienteer.core.component.meta.ODatabaseMetaPanel;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.Widget;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.components.TransactionlessForm;
import ru.ydn.wicket.wicketorientdb.model.CurrentDatabaseModel;

/**
 * Widget to configure database attributes
 */
@Widget(domain="schema", tab="database", id="odatabase-configuration", order=40, autoEnable=true)
public class ODatabaseConfigurationWidget extends AbstractModeAwareWidget<Void> {

    private OrienteerStructureTable<ODatabase<?>, String> structureTable;

    public ODatabaseConfigurationWidget(String id, IModel<Void> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
        Form<ODatabase<?>> form = new Form<ODatabase<?>>("form");
        structureTable  = new OrienteerStructureTable<ODatabase<?>, String>("attributes", CurrentDatabaseModel.<ODatabase<?>>getInstance(), ODatabaseMetaPanel.ODATABASE_ATTRS) {

            @Override
            protected Component getValueComponent(String id, final IModel<String> rowModel) {
                return new ODatabaseMetaPanel<Object>(id, getModeModel(), getModel(), rowModel);
            }

        };
        structureTable.addCommand(new EditSchemaCommand<ODatabase<?>>(structureTable, getModeModel()));
        structureTable.addCommand(new SaveSchemaCommand<ODatabase<?>>(structureTable, getModeModel()));

        form.add(structureTable);
        add(form);
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.bars);
    }

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return new ResourceModel("database.configuration");
    }
    
    @Override
    protected String getWidgetStyleClass() {
    	return "strict";
    }
}
