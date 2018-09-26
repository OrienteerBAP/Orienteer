package org.orienteer.architect.component.panel;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.architect.component.panel.command.AddOClassesCommand;
import org.orienteer.architect.event.CloseModalWindowEvent;
import org.orienteer.architect.model.OArchitectOClass;
import org.orienteer.architect.util.OArchitectClassesUtils;
import org.orienteer.architect.util.OArchitectJsUtils;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxFormCommand;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.component.property.BooleanEditPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.CheckBoxColumn;
import org.orienteer.core.component.table.OClassColumn;
import org.orienteer.core.component.table.OClassMetaColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.component.GenericTablePanel;
import org.orienteer.core.service.IFilterPredicateFactory;
import ru.ydn.wicket.wicketorientdb.converter.OClassClassNameConverter;
import ru.ydn.wicket.wicketorientdb.model.AbstractJavaSortableDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OClassesDataProvider;
import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper;

import java.util.List;
import java.util.Optional;

/**
 * Panel which represents list of classes for orienteer-architect editor
 */
public class SchemaOClassesModalPanel extends Panel {

    private final IModel<List<OArchitectOClass>> existClasses;

    public SchemaOClassesModalPanel(String id, IModel<List<OArchitectOClass>> existClasses) {
        super(id);
        this.existClasses = existClasses;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(createGenericTablePanel("tablePanel"));
        setOutputMarkupPlaceholderTag(true);
    }

    private GenericTablePanel<OClass> createGenericTablePanel(String id) {
        AbstractJavaSortableDataProvider<OClass, String> provider = getProvider();
        provider.setSort("name", SortOrder.ASCENDING);
        List<IColumn<OClass, String>> columns = getColumns();
        GenericTablePanel<OClass> tablePanel = new GenericTablePanel<>(id, columns, provider, 20);
        addCommands(tablePanel.getDataTable());
        return tablePanel;
    }

    private AbstractJavaSortableDataProvider<OClass, String> getProvider() {
        Predicate<OClass> predicate = OrienteerWebApplication.get().getServiceInstance(IFilterPredicateFactory.class)
                .getGuicePredicateForClassesView(Model.of(true));
        return new OClassesDataProvider(predicate);
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
        table.addCommand(createAddClassesCommand(table));
        table.addCommand(createCancelCommand(table));
    }

    private Command<OClass> createAddClassesCommand(OrienteerDataTable<OClass, String> table) {
        return new AddOClassesCommand(new ResourceModel("widget.architect.editor.list.classes.command.add"), table) {
            @Override
            protected void performAction(AjaxRequestTarget target, String json) {
                executeCallback(target, json);
                send(getParent(), Broadcast.BUBBLE, new CloseModalWindowEvent(target, SchemaOClassesModalPanel.this::onModalClose));
            }
        };
    }

    private Command<OClass> createCancelCommand(OrienteerDataTable<OClass, String> table) {
        return new AjaxFormCommand<OClass>(new ResourceModel("widget.architect.editor.list.classes.command.cancel"), table) {

            @Override
            protected void onInstantiation() {
                super.onInstantiation();
                setBootstrapType(BootstrapType.DANGER);
                setIcon(FAIconType.times);
            }

            @Override
            public void onClick(Optional<AjaxRequestTarget> targetOptional) {
                if (targetOptional.isPresent()) {
                    AjaxRequestTarget target = targetOptional.get();
                    executeCallback(target, "null");
                    SchemaOClassesModalPanel.this.send(
                            getParent(),
                            Broadcast.BUBBLE,
                            new CloseModalWindowEvent(target, SchemaOClassesModalPanel.this::onModalClose)
                    );
                }
            }
        };
    }

    private IColumn<OClass, String> createCheckBoxColumn() {
        return new CheckBoxColumn<OClass, String, String>(OClassClassNameConverter.INSTANCE) {
            @Override
            public void populateItem(Item<ICellPopulator<OClass>> cellItem, String componentId, final IModel<OClass> rowModel) {
                cellItem.add(new BooleanEditPanel(componentId, getCheckBoxModel(rowModel)) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        List<OArchitectOClass> classes = existClasses.getObject();
                        if (classes != null) {
                            setEnabled(!OArchitectClassesUtils.isClassContainsIn(rowModel.getObject().getName(), classes));
                        }
                    }
                });
            }
        };
    }

    private void onModalClose(AjaxRequestTarget target) {
        target.appendJavaScript(OArchitectJsUtils.switchPageScroll(false));
    }

    private void executeCallback(AjaxRequestTarget target, String json) {
        target.appendJavaScript(String.format(OArchitectJsUtils.callback(), json));
    }
}
