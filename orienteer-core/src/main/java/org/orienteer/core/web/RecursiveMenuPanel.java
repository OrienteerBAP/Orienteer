package org.orienteer.core.web;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.model.ODocumentNameModel;
import org.orienteer.core.module.PerspectivesModule;

import java.util.List;

/**
 * Panel to display recursive tree menu.
 */
public class RecursiveMenuPanel extends GenericPanel<ODocument> {
	
	private int level = -1;
	//hasActive

    public RecursiveMenuPanel(String id, IModel<ODocument> itemModel) {
        super(id, itemModel);
        setOutputMarkupId(true);
        add(new ListView<ODocument>("items", new PropertyModel<List<ODocument>>(this, "items")) {

            @Override
            protected void populateItem(ListItem<ODocument> item) {
                IModel<ODocument> itemModel = item.getModel();
                IModel<String> urlModel = new PropertyModel<String>(itemModel, "url");
                IModel<List<ODocument>> subItems = new PropertyModel<List<ODocument>>(itemModel, "subItems");
                final boolean hasSubItems = subItems.getObject() != null && !subItems.getObject().isEmpty();
                ExternalLink link = new ExternalLink("link", urlModel)
                        .setContextRelative(true);
                link.add(new FAIcon("icon", new PropertyModel<String>(itemModel, "icon")),
                        new Label("name", new ODocumentNameModel(item.getModel())).setRenderBodyOnly(true));
                item.add(link);
                if (isActiveItem(urlModel)) {
                   	link.add(new AttributeAppender("class", " active"));
                }
                if (hasSubItems){
                    item.add(new AttributeAppender("class", " nav-dropdown"));
                	link.add(new AttributeAppender("class", " nav-dropdown-toggle"));
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
	    	if(doc.getSchemaClass().isSubClassOf(PerspectivesModule.IOPerspective.CLASS_NAME)) {
	    		items = doc.field("menu");
	    	} else if(doc.getSchemaClass().isSubClassOf(PerspectivesModule.IOPerspectiveItem.CLASS_NAME)) {
	    		items = doc.field("subItems");
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
    	/*
    	String addClass = level==1?"nav-first-level":
    						(level==2?"nav-second-level":
    							(level==3?"nav-third-level":"nav-"+level+"-level"));
    							
    	tag.append("class", addClass, " ");*/
    	//tag.append("class", "nav-dropdown-items", " ");
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

    private boolean isActiveItem(IModel<String> urlModel) {
        String currentUrl = RequestCycle.get().getRequest().getUrl().getPath();
        String url = urlModel.getObject();
        return url!=null && currentUrl.equals(url.replaceFirst("^/", ""));
    }
}
