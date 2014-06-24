package ru.ydn.orienteer.web;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import ru.ydn.orienteer.components.properties.DefaultViewPanel;
import ru.ydn.orienteer.components.properties.MetaViewPanel;
import ru.ydn.orienteer.model.DynamicPropertyValueModel;

import com.orientechnologies.common.thread.OPollerThread;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

@MountPath("/view/#{rid}")
public class ViewDocumentPage extends DocumentPage {

	public ViewDocumentPage(IModel<ODocument> model) {
		super(model);
	}

	public ViewDocumentPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	public void initialize() {
		super.initialize();
		ListView<OProperty> properties = new ListView<OProperty>("properties", 
								new PropertyModel<List<OProperty>>(getDocumentModel(), "schemaClass.properties()")) {

			@Override
			protected void populateItem(
					ListItem<OProperty> item) {
				item.add(new Label("name", new PropertyModel<String>(item.getModel(), "name")));
				item.add(new MetaViewPanel<Object>("value", getDocumentModel(), item.getModel()));
			}
		};
		add(properties);
	}
	
	

}
