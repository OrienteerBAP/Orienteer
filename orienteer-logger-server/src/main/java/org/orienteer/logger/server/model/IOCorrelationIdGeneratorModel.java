package org.orienteer.logger.server.model;

import java.util.Map;

import org.apache.wicket.core.util.lang.WicketObjects;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.dao.OrienteerOProperty;
import org.orienteer.logger.IOCorrelationIdGenerator;
import org.orienteer.transponder.annotation.EntityProperty;
import org.orienteer.transponder.annotation.EntityPropertyIndex;
import org.orienteer.transponder.annotation.EntityType;
import org.orienteer.transponder.orientdb.IODocumentWrapper;
import org.orienteer.transponder.orientdb.ODriver;
import org.orienteer.transponder.orientdb.OrientDBProperty;

import com.google.inject.ProvidedBy;

/**
 * Wrapper for correlation id generator
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(value = IOCorrelationIdGeneratorModel.CLASS_NAME)
public interface IOCorrelationIdGeneratorModel extends IODocumentWrapper {

    public static final String CLASS_NAME = "OCorrelationIdGenerator";

    @OrienteerOProperty(visualization = UIVisualizersRegistry.VISUALIZER_LOCALIZATION)
    @OrientDBProperty(notNull = true)
    public Map<String,String> getName();
	public IOCorrelationIdGeneratorModel setName(Map<String,String> value);
	
	@OrientDBProperty(notNull = true)
	@EntityPropertyIndex(type=ODriver.OINDEX_UNIQUE)
    public String getAlias();
	public IOCorrelationIdGeneratorModel setAlias(String value);
	
	@EntityProperty("class")
	@OrientDBProperty(notNull = true)
	public String getCorrelationClassName();
	@EntityProperty(value = "class")
	@OrientDBProperty(notNull = true)
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
