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
import ru.ydn.wicket.wicketconsole.IScriptEngineInterlayerResult;
import ru.ydn.wicket.wicketconsole.IScriptEngineInterlayerResultRenderer;

/**
 * Renderer for {@link ODBScriptEngineInterlayerResult}  
 */
public class ODBScriptEngineInterlayerResultRenderer implements IScriptEngineInterlayerResultRenderer{

	
	public ODBScriptEngineInterlayerResultRenderer() {
	}

	@Override
	public Component getErrorView(String id, IModel<IScriptEngineInterlayerResult> data) {
		return new MultiLineLabel(id,new PropertyModel<>(data, "error")).add(HideIfObjectIsEmptyBehavior.INSTANCE);
	}

	@Override
	public Component getOutView(String id,IModel<IScriptEngineInterlayerResult> data) {
		Object dataObj = data.getObject().getReturnedObject();
		if (dataObj instanceof OResultSet){
			int size = ((OResultSet)dataObj).size(); 
			if (size == 0){
				return getEmptyView(id);
			}else{
				return getListView(id,data);
			}
		}else{
			return new Label(id,"").setVisibilityAllowed(false);
		} 
	}
	
	private Component getEmptyView(String name){
		return new Label(name,new ResourceModel("devutils.console.listIsEmpty", "List is empty"));
	}

	private Component getListView(String id, IModel<IScriptEngineInterlayerResult> data){
		return new RendererListViewComponent(id,data);
	}


	
}
