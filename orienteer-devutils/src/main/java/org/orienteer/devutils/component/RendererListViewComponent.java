package org.orienteer.devutils.component;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.orienteer.devutils.component.ODBScriptEngineInterlayerResultModel.ODBScriptResultModelType;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class RendererListViewComponent extends Panel{

	private static final long serialVersionUID = 1L;

	public RendererListViewComponent(String id, IModel<ODBScriptEngineInterlayerResult> model) {
		super(id, model);
		
		add(
			new ListView<String>("headerList", (IModel) new ODBScriptEngineInterlayerResultModel(model,ODBScriptResultModelType.TITLE_LIST)){
				@Override
				protected void populateItem(ListItem<String> item) {
					item.add(new Label("fieldName",item.getDefaultModelObjectAsString()));
				}
			}
		);

		add(new ListView<ODocument>("documentList",(IModel) new ODBScriptEngineInterlayerResultModel(model,ODBScriptResultModelType.VALUE_LIST)){
			@Override
			protected void populateItem(ListItem<ODocument> item) {
				item.add(
					new ListView<Object>("fieldList", new ODBScriptEngineInterlayerResultItemModel(item.getModel())){
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
