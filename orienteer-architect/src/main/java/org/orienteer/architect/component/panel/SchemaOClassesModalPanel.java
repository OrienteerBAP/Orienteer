package org.orienteer.architect.component.panel;

import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.architect.component.panel.command.AddOClassesCommand;
import org.orienteer.architect.component.panel.command.CancelCommand;
import org.orienteer.architect.util.OArchitectOClass;
import org.orienteer.core.component.property.BooleanEditPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.CheckBoxColumn;
import org.orienteer.core.component.table.OClassColumn;
import org.orienteer.core.component.table.OClassMetaColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.component.GenericTablePanel;
import org.orienteer.core.component.widget.schema.OClassesWidget;
import ru.ydn.wicket.wicketorientdb.converter.OClassClassNameConverter;
import ru.ydn.wicket.wicketorientdb.model.AbstractJavaSortableDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OClassesDataProvider;
import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Panel which represents list of classes for orienteer-architect editor
 */
public class SchemaOClassesModalPanel extends Panel implements IOClassesModalManager {

    private OrienteerDataTable<OClass, String> table;
    private ModalWindow modal;
    private List<OArchitectOClass> existsClasses;

    private final String jsCallback;

    public SchemaOClassesModalPanel(String id, String jsCallback) {
        super(id);
        this.jsCallback = jsCallback;
        modal = createModalWindow("modal");
        modal.setContent(createGenericTablePanel(modal.getContentId()));
        modal.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            @Override
            public void onClose(AjaxRequestTarget target) {
                switchPageScroll(target, false);
            }
        });
        add(modal);
    }

    private ModalWindow createModalWindow(String id) {
        ModalWindow modal = new ModalWindow(id);
        modal.setOutputMarkupId(true);
        modal.setTitle(new ResourceModel("widget.architect.editor.list.classes.title"));
        modal.setInitialWidth(670);
        modal.setInitialHeight(510);
        modal.setMinimalWidth(670);
        modal.setMinimalHeight(510);
        return modal;
    }

    private GenericTablePanel<OClass> createGenericTablePanel(String id) {
        AbstractJavaSortableDataProvider<OClass, String> provider = getProvider();
        provider.setSort("name", SortOrder.ASCENDING);
        List<IColumn<OClass, String>> columns = getColumns();
        GenericTablePanel<OClass> tablePanel = new GenericTablePanel<>(id, columns, provider, 20);
        table = tablePanel.getDataTable();
        addCommands(table);
        return tablePanel;
    }

    private AbstractJavaSortableDataProvider<OClass, String> getProvider() {
        return new OClassesDataProvider(new OClassesWidget.FilterClassesPredicate(Model.of(false)));
    }

    private List<IColumn<OClass, String>> getColumns() {
        List<IColumn<OClass, String>> columns = Lists.newArrayList();
        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        columns.add(createCheckBoxColumn());
        columns.add(new OClassColumn(OClassPrototyper.NAME, modeModel));
        columns.add(new OClassMetaColumn(OClassPrototyper.SUPER_CLASSES, modeModel));
        columns.add(new OClassMetaColumn(OClassPrototyper.ABSTRACT, modeModel));
        columns.add(new OClassMetaColumn(OClassPrototyper.STRICT_MODE, modeModel));
        return columns;
    }

    private void addCommands(OrienteerDataTable<OClass, String> table) {
        table.addCommand(new AddOClassesCommand(
                new ResourceModel("widget.architect.editor.list.classes.command.add"), this));
        table.addCommand(new CancelCommand(
                new ResourceModel("widget.architect.editor.list.classes.command.cancel"), this));
    }


    private IColumn<OClass, String> createCheckBoxColumn() {
        return new CheckBoxColumn<OClass, String, String>(OClassClassNameConverter.INSTANCE) {
            @Override
            public void populateItem(Item<ICellPopulator<OClass>> cellItem, String componentId, final IModel<OClass> rowModel) {
                cellItem.add(new BooleanEditPanel(componentId, getCheckBoxModel(rowModel)) {
                    @Override
                    public boolean isEnabled() {
                        return !containsInExistsClasses(rowModel.getObject().getName());
                    }
                });
            }
        };
    }

    private boolean containsInExistsClasses(String name) {
        boolean contains = false;
        for (OArchitectOClass oClass : existsClasses) {
            if (oClass.getName().equals(name)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    @Override
    public void setExistsClasses(List<OArchitectOClass> classes) {
        this.existsClasses = classes;
    }

    @Override
    public void executeCallback(AjaxRequestTarget target, String json) {
        target.appendJavaScript(String.format(jsCallback, json));
    }

    @Override
    public void showModalWindow(AjaxRequestTarget target) {
        if (!modal.isShown()) {
            modal.show(target);
            switchPageScroll(target, true);
        }
    }

    @Override
    public void closeModalWindow(AjaxRequestTarget target) {
        if (modal.isShown()) {
            modal.close(target);
        }
    }

    @Override
    public List<OClass> getAllClasses() {
        List<OClass> classes = Lists.newArrayList();
        IDataProvider<OClass> dataProvider = table.getDataProvider();
        Iterator<? extends OClass> iterator = dataProvider.iterator(0, dataProvider.size());
        while (iterator.hasNext()) {
            classes.add(iterator.next());
        }
        return classes;
    }

    @Override
    public List<OArchitectOClass> toOArchitectOClasses(List<OClass> classes) {
        List<OArchitectOClass> architectOClasses = new ArrayList<>(classes.size());
        for (OClass oClass : classes) {
            architectOClasses.add(OArchitectOClass.toArchitectOClass(oClass));
        }
        return architectOClasses;
    }

    private void switchPageScroll(AjaxRequestTarget target, boolean show) {
        target.appendJavaScript(String.format("app.editor.fullScreenEnable = %s; app.switchPageScrolling();", !show));
    }

    @Override
    public OrienteerDataTable<OClass, String> getTable() {
        return table;
    }
}
