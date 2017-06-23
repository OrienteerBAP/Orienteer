package org.orienteer.core.method.filters;

import java.util.ArrayList;
import java.util.List;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.IMethodFilter;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * 
 * OFilter by OClass of current ODocument or OClass
 * Support multiple values splitted by "|"
 *
 */

public class OEntityFilter implements IMethodFilter {

	private List<OClass> oClasses;
	private String filterData; 
	
	@Override
	public IMethodFilter setFilterData(String filterData) {
		this.filterData = filterData;
		return this;
	}
	
	private List<OClass> getOClasses(){
		if (oClasses==null){
			String[] strOClasses = filterData.split("\\|");
			if (strOClasses.length>0){
				oClasses = new ArrayList<OClass>(strOClasses.length);
				for (String strClass : strOClasses) {
					OClass oClass = OrienteerWebApplication.get().getDatabase().getMetadata().getSchema().getClass(strClass);
					if (oClass!=null){
						oClasses.add(oClass);
					}
				}
			}else{
				//TODO insert empty list class here
				oClasses = new ArrayList<OClass>(0);
			}
		}
		return oClasses;
	}
	
	@Override
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject) {
		OClass oclass = getOClass(dataObject);
		if (oclass!=null){
			for (OClass oClass : getOClasses()) {
				if (oclass.isSubClassOf(oClass)){
					return true;
				}
			}
		}
		return false;
	}
	
	private OClass getOClass(IMethodEnvironmentData dataObject){
		if (dataObject.getDisplayObjectModel()!=null){
			Object obj = dataObject.getDisplayObjectModel().getObject();
			if (obj instanceof OClass){
				return (OClass) obj;
			}else if (obj instanceof ODocument){
				return ((ODocument)obj).getSchemaClass();
			}
		}
		return null;
	}
}
