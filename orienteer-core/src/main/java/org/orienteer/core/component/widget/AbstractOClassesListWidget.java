package org.orienteer.core.component.widget;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.BookmarkablePageLinkCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.CheckBoxColumn;
import org.orienteer.core.component.table.OClassColumn;
import org.orienteer.core.component.table.OClassMetaColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.component.GenericTablePanel;
import org.orienteer.core.web.BrowseOClassPage;
import org.orienteer.core.widget.AbstractWidget;

import ru.ydn.wicket.wicketorientdb.converter.OClassClassNameConverter;
import ru.ydn.wicket.wicketorientdb.model.AbstractJavaSortableDataProvider;
import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper;

import java.util.ArrayList;
import java.util.List;

/**
 * Widget displaying list of {@link OClass}es
 * @param <T> the type of main data object linked to this widget
 */
public abstract class AbstractOClassesListWidget<T> extends AbstractWidget<T> {

    public AbstractOClassesListWidget(String id, IModel<T> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);

        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        List<IColumn<OClass, String>> columns = new ArrayList<IColumn<OClass,String>>();
        columns.add(new CheckBoxColumn<OClass, String, String>(OClassClassNameConverter.INSTANCE));
        columns.add(new OClassColumn(OClassPrototyper.NAME, modeModel));
        columns.add(new OClassMetaColumn(OClassPrototyper.SUPER_CLASSES, modeModel));
        columns.add(new OClassMetaColumn(OClassPrototyper.ABSTRACT, modeModel));
        columns.add(new OClassMetaColumn(OClassPrototyper.STRICT_MODE, modeModel));
        columns.add(new PropertyColumn<OClass, String>(new ResourceModel("class.count"), "count", "count"));
        columns.add(new AbstractColumn<OClass, String>(new ResourceModel("class.browse")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void populateItem(Item<ICellPopulator<OClass>> cellItem,
                                     String componentId, final IModel<OClass> rowModel) {
                cellItem.add(new BookmarkablePageLinkCommand<OClass>(componentId, "class.browse", BrowseOClassPage.class) {
                    public PageParameters getPageParameters() {
                        return BrowseOClassPage.preparePageParameters(rowModel.getObject(), DisplayMode.VIEW);
                    }
                }.setIcon(FAIconType.angle_double_down).setBootstrapType(BootstrapType.INFO));
            }
        });
        AbstractJavaSortableDataProvider<OClass, String> provider = getOClassesDataProvider();
        provider.setSort("name", SortOrder.ASCENDING);
        GenericTablePanel<OClass> tablePanel = new GenericTablePanel<OClass>("tablePanel", columns, provider ,20);
        OrienteerDataTable<OClass, String> table = tablePanel.getDataTable();
        addTableCommands(table, modeModel);
        add(tablePanel);
    }

    protected abstract void addTableCommands(OrienteerDataTable<OClass, String> table, IModel<DisplayMode> modeModel);

    protected abstract AbstractJavaSortableDataProvider getOClassesDataProvider();
}
