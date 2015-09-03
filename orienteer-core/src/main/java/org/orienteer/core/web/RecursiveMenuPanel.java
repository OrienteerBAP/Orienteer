package org.orienteer.core.web;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.model.ODocumentNameModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;

import java.util.List;

/**
 * Panel to display recursive tree menu.
 */
public class RecursiveMenuPanel extends GenericPanel<ODocument> {

    @Override
    public void renderHead(IHeaderResponse response) {
            response.render(OnDomReadyHeaderItem.forScript(
                    "var cur = $(\"li.active\");" +
                    "cur.parents('ul').collapse('show');" +
                    "cur.parents('li').addClass(\"active\");"));
    }

    public RecursiveMenuPanel(String id, ODocumentPropertyModel<List<ODocument>> model) {
        super(id);

        add(new ListView<ODocument>("items", model) {

            @Override
            protected void populateItem(ListItem<ODocument> item) {
                IModel<ODocument> itemModel = item.getModel();
                ODocumentPropertyModel<String> urlModel = new ODocumentPropertyModel<String>(itemModel, "url");
                ODocumentPropertyModel<List<ODocument>> subItems = new ODocumentPropertyModel<List<ODocument>>(itemModel, "subItems");
                final boolean hasSubItems = subItems.getObject() != null && !subItems.getObject().isEmpty();
                ExternalLink link = new ExternalLink("link", urlModel)
                        .setContextRelative(true);
                link.add(new FAIcon("icon", new ODocumentPropertyModel<String>(itemModel, "icon")),
                        new Label("name", new ODocumentNameModel(item.getModel())).setRenderBodyOnly(true),
                        new WebMarkupContainer("menuLevelGlyph").setVisibilityAllowed(hasSubItems));
                item.add(link);
                if (isActiveItem(urlModel)) {
                    item.add(new AttributeModifier("class", "active"));
                }

                item.add(new ListView<ODocument>("subItems", subItems) {
                    @Override
                    protected void populateItem(ListItem<ODocument> subItem) {
                        IModel<ODocument> itemModel = subItem.getModel();
                        ODocumentPropertyModel<List<ODocument>> subSubItemsModel = new ODocumentPropertyModel<List<ODocument>>(itemModel, "subItems");
                        ODocumentPropertyModel<String> urlModel = new ODocumentPropertyModel<String>(itemModel, "url");
                        ExternalLink link = new ExternalLink("subItemLink", urlModel)
                                .setContextRelative(true);

                        if (isActiveItem(urlModel)) {
                            subItem.add(new AttributeModifier("class", "active"));
                        }
                        boolean hasSubSubItems = subSubItemsModel.getObject() != null && !subSubItemsModel.getObject().isEmpty();
                        link.add(new FAIcon("subItemIcon", new ODocumentPropertyModel<String>(itemModel, "icon")),
                                new Label("subItemName", new ODocumentNameModel(subItem.getModel())).setRenderBodyOnly(true),
                                new WebMarkupContainer("subItemGlyph").setVisibilityAllowed(hasSubSubItems));
                        subItem.add(link);

                        if(hasSubSubItems) {
                            subItem.add(new RecursiveMenuPanel("nestedItems", subSubItemsModel));
                        } else {
                            subItem.add(new EmptyPanel("nestedItems").setVisible(false));
                        }
                    }
                }.setVisibilityAllowed(hasSubItems));
            }
        });
    }

    private boolean isActiveItem(ODocumentPropertyModel<String> urlModel) {
        String currentUrl = RequestCycle.get().getRequest().getUrl().getPath();
        return currentUrl.equals(urlModel.getObject().replaceFirst("^/", ""));
    }
}
