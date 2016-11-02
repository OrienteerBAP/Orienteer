package org.orienteer.devutils.component;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OResultSet;

public class ODBScriptEngineInterlayerResultModel extends AbstractReadOnlyModel<List<?>>{

	public enum ODBScriptResultModelType{
		TITLE_LIST,VALUE_LIST
	}

	IModel<ODBScriptEngineInterlayerResult> resultModel;
	ODBScriptResultModelType type;
			
	public ODBScriptEngineInterlayerResultModel(IModel<ODBScriptEngineInterlayerResult> resultModel,ODBScriptResultModelType type) {
		this.resultModel = resultModel;
		this.type = type;
	}

	@Override
	public List<?> getObject() {
		
		ODBScriptEngineInterlayerResult resultObj = resultModel.getObject();
	    
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
