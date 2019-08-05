package org.orienteer.core.component.widget.browse;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.component.GenericTablePanel;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.model.JavaSortableDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;

import java.util.List;
import java.util.Map;

/**
 * Widget for display list of {@link ODocument} which are passed in constructor
 */
@Widget(id="list-documents", domain="documents", tab="list", autoEnable=true)
public class ListDocumentsWidget extends AbstractWidget<List<ODocument>> {

    @Inject
    private IOClassIntrospector oClassIntrospector;


    public ListDocumentsWidget(String id, IModel<List<ODocument>> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        OClass oClass = getOClass();
        JavaSortableDataProvider<ODocument, String> provider = new JavaSortableDataProvider<>(getModel());

        GenericTablePanel<ODocument> tablePanel =
                new GenericTablePanel<>("tablePanel", oClassIntrospector.getColumnsFor(oClass, true, modeModel), provider, 20);

        adjustTable(tablePanel.getDataTable(), modeModel, new OClassModel(oClass));
        add(tablePanel);
        add(UpdateOnActionPerformedEventBehavior.INSTANCE_ALL_CONTINUE);
    }

    private void adjustTable(OrienteerDataTable<ODocument, String> table, IModel<DisplayMode> modeModel, IModel<OClass> oClassModel) {
        table.getCommandsToolbar().setDefaultModel(getModel());
        Map<String, Command<ODocument>> commands = oClassIntrospector.getCommandsForDocumentsTable(table, modeModel, oClassModel);
        commands.forEach((key, command) -> table.addCommand(command));
    }

    private OClass getOClass() {
        List<ODocument> modelObject = getModelObject();
        return modelObject.get(0).getSchemaClass();
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.list_alt);
    }

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return new StringResourceModel("documents.list.title");
    }

    @Override
    protected String getWidgetStyleClass() {
        return "strict";
    }
}
