package org.orienteer.devutils.component;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class ODBScriptEngineInterlayerResultItemModel extends AbstractReadOnlyModel<List<Object>>{

	IModel<ODocument> itemModel;
	
	public ODBScriptEngineInterlayerResultItemModel(IModel<ODocument> itemModel) {
		this.itemModel = itemModel;
	}

	@Override
	public List<Object> getObject() {
		return Arrays.asList(itemModel.getObject().fieldValues());
	}

}
