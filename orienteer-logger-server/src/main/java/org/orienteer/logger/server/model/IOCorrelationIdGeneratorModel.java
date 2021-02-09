package org.orienteer.logger.server.model;

import java.util.Map;

import org.apache.wicket.core.util.lang.WicketObjects;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOFieldIndex;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.logger.IOCorrelationIdGenerator;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * Wrapper for correlation id generator
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IOCorrelationIdGeneratorModel.CLASS_NAME)
public interface IOCorrelationIdGeneratorModel extends IODocumentWrapper {

    public static final String CLASS_NAME = "OCorrelationIdGenerator";

    @DAOField(visualization = UIVisualizersRegistry.VISUALIZER_LOCALIZATION, notNull = true)
    public Map<String,String> getName();
	public IOCorrelationIdGeneratorModel setName(Map<String,String> value);
	
	@DAOField(notNull = true)
	@DAOFieldIndex(type=OClass.INDEX_TYPE.UNIQUE)
    public String getAlias();
	public IOCorrelationIdGeneratorModel setAlias(String value);
	
	@DAOField(value = "class", notNull = true)
	public String getCorrelationClassName();
	@DAOField(value = "class", notNull = true)
	public IOCorrelationIdGeneratorModel setCorrelationClassName(String value);
	

    public default <T extends IOCorrelationIdGenerator> Class<T> getCorrelationClass() {
    	String className = getCorrelationClassName();
    	return className!=null?WicketObjects.resolveClass(className):null;
    }

    public default <T extends IOCorrelationIdGenerator> T createCorrelationIdGenerator() {
    	String className = getCorrelationClassName();
    	return className!=null?WicketObjects.newInstance(className):null;
    }

    public default IOCorrelationIdGeneratorModel setCorrelationClass(Class<? extends IOCorrelationIdGenerator> clazz) {
        return setCorrelationClassName(clazz != null ? clazz.getName() : null);
    }
}
