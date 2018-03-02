package org.orienteer.core.web;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
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

import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;

public class FlatMenuPanel extends GenericPanel<ODocument>{
	private static final long serialVersionUID = 1L;
	private String itemsFieldName;

	public FlatMenuPanel(String id, IModel<ODocument> itemModel, String itemsFieldName) {
		super(id, itemModel);
        this.itemsFieldName = itemsFieldName;
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
                link.add(new Label("name", new ODocumentNameModel(item.getModel())).setRenderBodyOnly(true));
                item.add(link);
                if (isActiveItem(urlModel)) {
                   	link.add(new AttributeAppender("class", " active"));
                }
                if (hasSubItems){
                   	link.add(new AttributeModifier("aria-expanded", "false"));
                   	link.add(new AttributeModifier("aria-haspopup", "true"));
                   	link.add(new AttributeModifier("data-toggle", "dropdown"));
                			 
                    item.add(new AttributeAppender("class", " dropdown"));
                	link.add(new AttributeAppender("class", " dropdown-toggle"));
                }
            	item.add(new ListView<ODocument>("subItems", subItems) {
					@Override
					protected void populateItem(ListItem<ODocument> item) {
						item.setRenderBodyOnly(true);
		                IModel<ODocument> itemModel = item.getModel();
		                ODocumentPropertyModel<String> urlModel = new ODocumentPropertyModel<String>(itemModel, "url");
		                ExternalLink link = new ExternalLink("subItemLink", urlModel)
		                        .setContextRelative(true);
		                link.add(new Label("name", new ODocumentNameModel(item.getModel())).setRenderBodyOnly(true));
		                item.add(link);
					}
                });
            }
        });
	}

	@Override
    protected void onConfigure() {
    	super.onConfigure();
    	List<ODocument> subItems = getItems();
    	setVisible(subItems!=null && !subItems.isEmpty());
    }
    
    public List<ODocument> getItems() {
    	return getItems(getModelObject());
    }
    
    public List<ODocument> getItems(ODocument doc) {
    	List<ODocument> items = null;
    	if(doc!=null) {
    		items = doc.field(getItemsFieldName());
    	}
    	if(items!=null) items.remove(null); //Remove deleted records
    	return items;
    }
    
    public String getItemsFieldName() {
		return itemsFieldName;
	}
    
    private boolean isActiveItem(ODocumentPropertyModel<String> urlModel) {
        String currentUrl = RequestCycle.get().getRequest().getUrl().getPath();
        String url = urlModel.getObject();
        return url!=null && currentUrl.equals(url.replaceFirst("^/", ""));
    }
    
}
