package org.orienteer.devutils.component;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OResultSet;

public class RendererListViewComponent extends Panel{

	private static final long serialVersionUID = 1L;

	public RendererListViewComponent(String id, IModel<OResultSet> model) {
		super(id, model);
		ODocument exampleObject = (ODocument)((OResultSet)getDefaultModelObject()).get(0);
		
		List<String> fieldValues = Arrays.asList(exampleObject.fieldNames());
		add(
			new ListView<String>("headerList", fieldValues){
				@Override
				protected void populateItem(ListItem<String> item) {
					item.add(new Label("fieldName",item.getDefaultModelObjectAsString()));
				}
			}
		);

		add(new ListView<ODocument>("documentList",(List<ODocument>)model.getObject()){
			@Override
			protected void populateItem(ListItem<ODocument> item) {
				List<Object> fieldValues = Arrays.asList(item.getModelObject().fieldValues());
				item.add(
					new ListView<Object>("fieldList", fieldValues){
						@Override
						protected void populateItem(ListItem<Object> item) {
							item.add(new MultiLineLabel("value",item.getDefaultModelObjectAsString()));
						}
					}
				);
			}
		});
	}
}
