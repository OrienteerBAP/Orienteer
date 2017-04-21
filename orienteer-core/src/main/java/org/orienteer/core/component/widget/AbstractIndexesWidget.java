package org.orienteer.core.component.widget;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.DeleteOIndexCommand;
import org.orienteer.core.component.command.EditSchemaCommand;
import org.orienteer.core.component.command.SaveSchemaCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.CheckBoxColumn;
import org.orienteer.core.component.table.OIndexDefinitionColumn;
import org.orienteer.core.component.table.OIndexMetaColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.component.GenericTablePanel;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import ru.ydn.wicket.wicketorientdb.behavior.DisableIfPrototypeBehavior;
import ru.ydn.wicket.wicketorientdb.model.OIndexesDataProvider;
import ru.ydn.wicket.wicketorientdb.proto.OIndexPrototyper;
import ru.ydn.wicket.wicketorientdb.utils.OIndexNameConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for widgets showing and modifying {@link OIndex}es.
 *
 * @param <T> the type of main data object linked to this widget
 */
public abstract class AbstractIndexesWidget<T> extends AbstractModeAwareWidget<T> {

    protected final OrienteerDataTable<OIndex<?>, String> iTable;

    protected AbstractIndexesWidget(String id, IModel<T> model,
                                IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);

        IModel<DisplayMode> indexesDisplayMode = getModeModel();
        List<IColumn<OIndex<?>, String>> iColumns = new ArrayList<IColumn<OIndex<?>,String>>();
        iColumns.add(new CheckBoxColumn<OIndex<?>, String, String>(OIndexNameConverter.INSTANCE));
        iColumns.add(new OIndexDefinitionColumn(OIndexPrototyper.NAME, indexesDisplayMode));
        iColumns.add(new OIndexMetaColumn(OIndexPrototyper.TYPE, indexesDisplayMode));
        iColumns.add(new OIndexMetaColumn(OIndexPrototyper.DEF_FIELDS, indexesDisplayMode));
        iColumns.add(new OIndexMetaColumn(OIndexPrototyper.DEF_COLLATE, indexesDisplayMode));
        iColumns.add(new OIndexMetaColumn(OIndexPrototyper.DEF_NULLS_IGNORED, indexesDisplayMode));
        iColumns.add(new OIndexMetaColumn(OIndexPrototyper.SIZE, indexesDisplayMode));
        iColumns.add(new OIndexMetaColumn(OIndexPrototyper.KEY_SIZE, indexesDisplayMode));

        OIndexesDataProvider iProvider = getIndexDataProvider();
        iProvider.setSort("name", SortOrder.ASCENDING);
        GenericTablePanel<OIndex<?>> tablePanel = new GenericTablePanel<OIndex<?>>("tablePanel", iColumns, iProvider ,20);
        iTable = tablePanel.getDataTable();
        iTable.addCommand(new EditSchemaCommand<OIndex<?>>(iTable, indexesDisplayMode));
        iTable.addCommand(new SaveSchemaCommand<OIndex<?>>(iTable, indexesDisplayMode));
        iTable.addCommand(new DeleteOIndexCommand(iTable));
        iTable.setCaptionModel(new ResourceModel("class.indexes"));
        add(tablePanel);
        add(DisableIfPrototypeBehavior.INSTANCE, UpdateOnActionPerformedEventBehavior.INSTANCE_ALL_CONTINUE);
    }

    protected abstract String getCaptionResourceKey();

    protected abstract OIndexesDataProvider getIndexDataProvider();

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return new ResourceModel(getCaptionResourceKey());
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.list);
    }

    @Override
    protected String getWidgetStyleClass() {
        return "strict";
    }
}
