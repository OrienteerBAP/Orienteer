package org.orienteer.devutils.component;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

import com.orientechnologies.orient.core.sql.query.OResultSet;

import ru.ydn.wicket.wicketconsole.HideIfObjectIsEmptyBehavior;
import ru.ydn.wicket.wicketconsole.IScriptEngineInterlayerResultRenderer;

public class ODBScriptEngineInterlayerResultRenderer implements IScriptEngineInterlayerResultRenderer{

	private IModel<ODBScriptEngineInterlayerResult> data;
	
	public ODBScriptEngineInterlayerResultRenderer(IModel<ODBScriptEngineInterlayerResult> data) {
		this.data = data;
	}

	@Override
	public Component getErrorView(String name) {
		return new MultiLineLabel(name,new PropertyModel<>(data, "error")).add(HideIfObjectIsEmptyBehavior.INSTANCE);
	}

	@Override
	public Component getOutView(String name) {
		Object dataObj = data.getObject().getReturnedObject();
		if (dataObj instanceof OResultSet){
			int size = ((OResultSet)dataObj).size(); 
			if (size == 0){
				return getEmptyView(name);
			}else{
				return getListView(name);
			}
		}else{
			return new Label(name,"").setVisibilityAllowed(false);
		} 
	}
	
	private Component getEmptyView(String name){
		return new Label(name,new ResourceModel("devutils.console.listIsEmpty", "List is empty"));
	}

	private Component getListView(String name){
		return new RendererListViewComponent(name,data);
	} 
	
}
