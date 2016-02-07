package org.orienteer.core.component.widget.schema;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.OCluster;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.EditSchemaCommand;
import org.orienteer.core.component.command.SaveSchemaCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OClusterColumn;
import org.orienteer.core.component.table.OClusterMetaColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;

import ru.ydn.wicket.wicketorientdb.model.AbstractJavaSortableDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OClustersDataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Widget to list clusters in the schema
 */
@Widget(domain="schema", tab="clusters", id="list-oclusters", order=50, autoEnable=true)
public class OClustersWidget extends AbstractWidget<Void> {
    public static final String NAME = "name";
    public static final String COUNT = "recordsSize";
    public static final String CONFLICT_STRATEGY = "recordConflictStrategy";
    public static final String USE_WAL = "useWal";
    public static final String RECORD_GROW_FACTOR = "recordGrowFactor";
    public static final String RECORD_OVERFLOW_GROW_FACTOR = "recordOverflowGrowFactor";
    public static final String COMPRESSION = "compression";

    public OClustersWidget(String id, IModel<Void> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);

        Form<?> form = new Form<Object>("form");
        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        List<IColumn<OCluster, String>> columns = new ArrayList<IColumn<OCluster,String>>();
        columns.add(new OClusterColumn(NAME, modeModel));
        columns.add(new OClusterMetaColumn(CONFLICT_STRATEGY, modeModel));
        columns.add(new OClusterMetaColumn(COUNT, modeModel));

        AbstractJavaSortableDataProvider<OCluster, String> provider =  new OClustersDataProvider();
        provider.setSort(NAME, SortOrder.ASCENDING);

        OrienteerDataTable<OCluster, String> table = new OrienteerDataTable<OCluster, String>("clusters", columns, provider ,20);
        addTableCommands(table, modeModel);
        form.add(table);
        add(form);
    }

    private void addTableCommands(OrienteerDataTable<OCluster, String> table, IModel<DisplayMode> modeModel) {
        table.addCommand(new EditSchemaCommand<OCluster>(table, modeModel));
        table.addCommand(new SaveSchemaCommand<OCluster>(table, modeModel));
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.bars);
    }

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return new ResourceModel("cluster.list.title");
    }
    
    @Override
    protected String getWidgetStyleClass() {
    	return "strict";
    }
}
