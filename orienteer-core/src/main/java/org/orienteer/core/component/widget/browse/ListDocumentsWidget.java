package org.orienteer.core.component.widget.browse;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.*;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.component.GenericTablePanel;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.model.JavaSortableDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;

import java.util.List;

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
        IModel<OClass> oClassModel = new OClassModel(oClass);

        JavaSortableDataProvider<ODocument, String> provider = new JavaSortableDataProvider<>(getModel());

        GenericTablePanel<ODocument> tablePanel =
                new GenericTablePanel<>("tablePanel", oClassIntrospector.getColumnsFor(oClass, true, modeModel), provider, 20);

        final OrienteerDataTable<ODocument, String> table = tablePanel.getDataTable();
        table.getCommandsToolbar().setDefaultModel(getModel());
        table.addCommand(new EditODocumentsCommand(table, modeModel, oClassModel));
        table.addCommand(new SaveODocumentsCommand(table, modeModel));
        table.addCommand(new CopyODocumentCommand(table, oClassModel));
        table.addCommand(new DeleteODocumentCommand(table, oClassModel));
        table.addCommand(new ExportCommand<>(table, new PropertyModel<>(oClassModel, "name")));

        add(tablePanel);
        add(UpdateOnActionPerformedEventBehavior.INSTANCE_ALL_CONTINUE);
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
        return new StringResourceModel("class.browse.title", new OClassNamingModel(getOClass()));
    }

    @Override
    protected String getWidgetStyleClass() {
        return "strict";
    }
}
