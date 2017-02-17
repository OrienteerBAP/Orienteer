package org.orienteer.core.web;

import com.orientechnologies.orient.core.record.impl.ODocument;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.ComponentTag;
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
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.model.ODocumentNameModel;
import org.orienteer.core.module.PerspectivesModule;

import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;

import java.util.Collections;
import java.util.List;

/**
 * Panel to display recursive tree menu.
 */
public class RecursiveMenuPanel extends GenericPanel<ODocument> {
	
	private int level = -1;

    @Override
    public void renderHead(IHeaderResponse response) {
		if(level<=1) {
            response.render(OnDomReadyHeaderItem.forScript(
                    "var cur = $(\"#"+getMarkupId()+" li.active\");" +
                    "cur.parents('ul').collapse('show');" +
                    "cur.parents('li').addClass(\"active\");"));
		}
    }

    public RecursiveMenuPanel(String id, IModel<ODocument> itemModel) {
        super(id, itemModel);
        setOutputMarkupId(true);
        add(new ListView<ODocument>("items", new PropertyModel<List<ODocument>>(this, "items")) {

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
                item.add(new RecursiveMenuPanel("subItems", itemModel)); 
            }
        });
    }
    
    public List<ODocument> getItems() {
    	return getItems(getModelObject());
    }
    
    public List<ODocument> getItems(ODocument doc) {
    	List<ODocument> items = null;
    	if(doc!=null) {
	    	if(doc.getSchemaClass().isSubClassOf(PerspectivesModule.OCLASS_PERSPECTIVE)) {
	    		items = (List<ODocument>)doc.field("menu");
	    	} else if(doc.getSchemaClass().isSubClassOf(PerspectivesModule.OCLASS_ITEM)) {
	    		items = (List<ODocument>)doc.field("subItems");
	    	}
    	}
    	if(items!=null) items.remove(null); //Remove deleted records
    	return items;
    }
    
    @Override
    protected void onConfigure() {
    	super.onConfigure();
    	List<ODocument> subItems = getItems();
    	setVisible(subItems!=null && !subItems.isEmpty());
    }
    
    @Override
    protected void onComponentTag(ComponentTag tag) {
    	super.onComponentTag(tag);
    	String addClass = level==1?"nav-first-level":
    						(level==2?"nav-second-level":
    							(level==3?"nav-third-level":"nav-"+level+"-level"));
    	tag.append("class", addClass, " ");
    }
    
    @Override
    protected void onReAdd() {
    	super.onReAdd();
    	initLevel();
    }
    
    @Override
    protected void onInitialize() {
    	super.onInitialize();
    	initLevel();
    }
    
    protected int getLevel() {
    	if(level<0) initLevel();
    	return level;
    }
    
    private void initLevel() {
    	RecursiveMenuPanel parentMenuPanel = findParent(RecursiveMenuPanel.class);
    	level = parentMenuPanel==null?1:parentMenuPanel.getLevel()+1;
    }

    private boolean isActiveItem(ODocumentPropertyModel<String> urlModel) {
        String currentUrl = RequestCycle.get().getRequest().getUrl().getPath();
        String url = urlModel.getObject();
        return url!=null && currentUrl.equals(url.replaceFirst("^/", ""));
    }
}
