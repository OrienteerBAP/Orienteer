package org.orienteer.logger.server.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

import org.apache.wicket.core.util.lang.WicketObjects;
import org.joor.Reflect;
import org.joor.ReflectException;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOFieldIndex;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.logger.IOLoggerEventDispatcher;

import java.util.Collections;
import java.util.Map;

/**
 * Wrapper for event dispatcher
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IOLoggerEventDispatcherModel.CLASS_NAME)
public interface IOLoggerEventDispatcherModel extends IODocumentWrapper {

    public static final String CLASS_NAME = "OLoggerEventDispatcher";

    @DAOField(visualization = UIVisualizersRegistry.VISUALIZER_LOCALIZATION, notNull = true)
    public Map<String,String> getName();
	public IOLoggerEventDispatcherModel setName(Map<String,String> value);
	
	@DAOField(notNull = true)
	@DAOFieldIndex(type = OClass.INDEX_TYPE.UNIQUE)
    public String getAlias();
	public IOLoggerEventDispatcherModel setAlias(String value);
	
	@DAOField(notNull = true)
	public String getDispatcherClass();
	public IOLoggerEventDispatcherModel setDispatcherClass(String value);
	
    public default IOLoggerEventDispatcher createDispatcherClassInstance() {
    	String className = getDispatcherClass();
    	if(className==null) return null;
    	Reflect classReflect = Reflect.onClass(className);
    	try {
			return (IOLoggerEventDispatcher)classReflect.create(getAlias()).get();
		} catch (ReflectException e) {
			return (IOLoggerEventDispatcher)classReflect.create().get();
		}
    }
}
