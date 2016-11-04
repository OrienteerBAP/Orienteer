package org.orienteer.devutils.component;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OResultSet;

import ru.ydn.wicket.wicketconsole.IScriptEngineInterlayerResult;

/**
 * Wicket model for {@link ODBScriptEngineInterlayerResult} 
 *
 */

public class ODBScriptEngineInterlayerResultModel extends AbstractReadOnlyModel<List<?>>{

	/**
	 * Type of inner object of {@link ODBScriptEngineInterlayerResultModel}
	 *
	 */
	public enum ODBScriptResultModelType{
		TITLE_LIST,VALUE_LIST
	}

	IModel<IScriptEngineInterlayerResult> resultModel;
	ODBScriptResultModelType type;
			
	public ODBScriptEngineInterlayerResultModel(IModel<IScriptEngineInterlayerResult> data,ODBScriptResultModelType type) {
		this.resultModel = data;
		this.type = type;
	}

	@Override
	public List<?> getObject() {
		
		ODBScriptEngineInterlayerResult resultObj = (ODBScriptEngineInterlayerResult)resultModel.getObject();
	    
	    switch (type)
	    {
	 
	      case TITLE_LIST:
	 
	        return makeTitleList();
	 
	      case VALUE_LIST:
	        return makeValueList();
	 
	    }
	    throw new UnsupportedOperationException("invalid ODBScriptResultModelType = "
	                                            + type.name());	
	}
	
	private List<String> makeTitleList(){
		OResultSet returnedObject = (OResultSet)resultModel.getObject().getReturnedObject();
		ODocument exampleObject = (ODocument)returnedObject.get(0);
		
		List<String> fieldValues = Arrays.asList(exampleObject.fieldNames());
		return fieldValues;
	}

	private List<ODocument> makeValueList(){
		OResultSet returnedObject = (OResultSet)resultModel.getObject().getReturnedObject();
		return returnedObject;
	}

}
