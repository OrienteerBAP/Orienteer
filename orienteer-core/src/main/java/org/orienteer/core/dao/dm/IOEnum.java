package org.orienteer.core.dao.dm;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.dao.DAO;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.dao.OrienteerOProperty;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.transponder.annotation.EntityPropertyIndex;
import org.orienteer.transponder.annotation.EntityType;
import org.orienteer.transponder.annotation.Lookup;
import org.orienteer.transponder.orientdb.ODriver;

import com.google.inject.ProvidedBy;
import com.orientechnologies.common.util.OPair;

/**
 * DAO interface for classes which represents enumerations
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(value = IOEnum.CLASS_NAME, isAbstract = true)
public interface IOEnum {
	
	public static final String CLASS_NAME = "OEnum";

	@OrienteerOProperty(visualization = UIVisualizersRegistry.VISUALIZER_LOCALIZATION)
	public Map<String, String> getName();
	public IOEnum setName(Map<String, String> value);
	
	@EntityPropertyIndex(type = ODriver.OINDEX_UNIQUE)
	public String getAlias();
	public IOEnum setAlias(String value);
	
	@Lookup("select from :targetType where alias = :alias")
	public boolean lookupByAlias(String alias);
	
	public static void upsertOEnum(Class<? extends IOEnum> daoClass, String prefix, String... values) {
		upsertOEnum(daoClass, true, prefix, values);
	}
	
	public static void upsertOEnum(Class<? extends IOEnum> daoClass, boolean updateIfPresent, String prefix, String... values) {
		oEnumUpsertStream(Arrays.asList(values).stream(), daoClass, updateIfPresent, prefix)
			.forEach(DAO::save);
	}
	
	public static <E extends IOEnum> Stream<E> oEnumUpsertStream(Stream<String> values, Class<E> daoClass, boolean updateIfPresent, String prefix) {
		return values.map(n -> new OPair<String, E>(n, DAO.create(daoClass)))
			  .filter(p -> !p.getValue().lookupByAlias(p.getKey()) || updateIfPresent)
		      .map(p -> (E)p.getValue()
		    		  			.setName(CommonUtils.getLocalizedStrings(prefix+"."+p.getKey()))
		    		  		    .setAlias(p.getKey()));
	}
}
