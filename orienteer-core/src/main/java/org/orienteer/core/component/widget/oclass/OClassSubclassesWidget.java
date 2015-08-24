package org.orienteer.core.component.widget.oclass;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.*;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.widget.AbstractOClassesListWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.model.AbstractJavaSortableDataProvider;
import ru.ydn.wicket.wicketorientdb.model.AbstractListModel;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;

import java.util.Collection;

/**
 * Widget to show subclasses of a {@link OClass}
 */
@Widget(id="class-subclasses", domain="class", tab="subclasses", order=40, autoEnable=true)
public class OClassSubclassesWidget extends AbstractOClassesListWidget<OClass> {

    public OClassSubclassesWidget(String id, IModel<OClass> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.level_down);
    }

    @Override
    protected void addTableCommands(OrienteerDataTable<OClass, String> table, IModel<DisplayMode> modeModel) {
        table.addCommand(new CreateOClassCommand(table, getModelObject()));
        table.addCommand(new EditSchemaCommand<OClass>(table, modeModel));
        table.addCommand(new SaveSchemaCommand<OClass>(table, modeModel));
        table.addCommand(new DeleteOClassCommand(table));
    }

    @Override
    protected AbstractJavaSortableDataProvider<OClass, String> getOClassesDataProvider() {
        AbstractListModel<OClass> subclassListModel = new AbstractListModel<OClass>() {
            @Override
            protected Collection<OClass> getData() {
                return getModelObject().getSubclasses();
            }
        };

        return new AbstractJavaSortableDataProvider<OClass, String>(subclassListModel) {
            @Override
            public IModel<OClass> model(OClass object) {
                return new OClassModel(object);
            }
        };
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel("class.subclasses");
    }
}
