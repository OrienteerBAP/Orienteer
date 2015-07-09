package org.orienteer.core.component.property;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.OClassPageLink;

import java.util.List;

/**
 * {@link GenericPanel} to view a link to multiple {@link OClass}
 */
public class MultipleOClassesViewPanel extends GenericPanel<List<OClass>> {

    public MultipleOClassesViewPanel(String id, IModel<List<OClass>> model) {
        super(id, model);
        initialize();
    }

    private void initialize() {
        add(new ListView<OClass>("classes", getModel())
        {
            @Override
            protected void populateItem(ListItem item) {
                OClassPageLink classLink = new OClassPageLink("classLink", item.getModel());
                item.add(classLink);
                classLink.add(new Label("className", ((OClass)item.getModelObject()).getName()));
            }
        });
    }
}
