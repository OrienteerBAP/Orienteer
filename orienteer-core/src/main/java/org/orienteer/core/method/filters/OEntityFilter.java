package org.orienteer.core.method.filters;

import java.util.ArrayList;
import java.util.List;

import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.IMethodFilter;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
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
	
	@Override
	public IMethodFilter setFilterData(String filterData) {
		String[] strOClasses = filterData.split("\\|");
		if (strOClasses.length>0){
			oClasses = new ArrayList<OClass>(strOClasses.length);
			for (String strClass : strOClasses) {
				OClass oClass = ODatabaseRecordThreadLocal.INSTANCE.get().getMetadata().getSchema().getClass(strClass);
				if (oClass!=null){
					oClasses.add(oClass);
				}
			}
		}
		return this;
	}

	@Override
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject) {
		OClass oclass = getOClass(dataObject);
		if (oclass!=null){
			for (OClass oClass : oClasses) {
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
