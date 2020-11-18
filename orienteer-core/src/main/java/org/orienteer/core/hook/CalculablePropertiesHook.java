package org.orienteer.core.hook;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQLParsingException;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.CustomAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link ODocumentHookAbstract} for automatic calculation of some properties.
 * Properties to be automatically calculated should be marked by {@link CustomAttribute}.CALCULABLE
 * Logic for calculation should be stored in {@link CustomAttribute}.CALC_SCRIPT
 */
public class CalculablePropertiesHook extends ODocumentHookAbstract {

	private static final Logger LOG = LoggerFactory.getLogger(CalculablePropertiesHook.class);

	private final static Pattern FULL_QUERY_PATTERN = Pattern.compile("^\\s*(select|traverse)", Pattern.CASE_INSENSITIVE);
	
	private Map<String, Integer> schemaVersions                = new ConcurrentHashMap<String, Integer>();
	private Table<String, String, List<String>> calcProperties = HashBasedTable.create();

	public static final String VALUE = "value";
	
	public CalculablePropertiesHook(ODatabaseDocument database) {
		super(database);
	}

	@Override
	public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
		return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
	}
	
	@SuppressWarnings("deprecation")
	private List<String> getCalcProperties(ODocument iDocument)
	{
		ODatabaseDocument db = iDocument.getDatabase();
		OClass oClass = iDocument.getSchemaClass();
		if(db==null || oClass==null) return null;
		OSchema schema = db.getMetadata().getSchema();
		int schemaVersion = schema.getVersion();
		Integer prevSchemaVersion = schemaVersions.get(db.getURL());
		if(!Objects.equals(prevSchemaVersion, schemaVersion))
		{
			//Clear and fullfill cache
			calcProperties.row(db.getURL()).clear();
			for(OClass clazz: schema.getClasses())
			{
				List<String> calcProperties=null;
				for(OProperty property: clazz.properties())
				{
					if(CustomAttribute.CALCULABLE.getValue(property, false))
					{
						if(calcProperties==null) calcProperties = new ArrayList<String>();
						calcProperties.add(property.getName());
					}
				}
				if(calcProperties!=null) this.calcProperties.put(db.getURL(), clazz.getName(), calcProperties);
			}
			//Update schemaVersion
			schemaVersions.put(db.getURL(), schemaVersion);
		}
		return calcProperties.get(db.getURL(), oClass.getName());
	}
	
	@Override
	public RESULT onRecordBeforeCreate(ODocument iDocument) {
		return onRecordBeforeUpdate(iDocument);
	}

	@Override
	public RESULT onRecordBeforeUpdate(ODocument iDocument) {
		List<String> calcProperties = getCalcProperties(iDocument);
		if(calcProperties!=null && calcProperties.size()>0)
		{
			boolean wasChanged=false;
			String[] fieldNames = iDocument.fieldNames();
			for (String field : fieldNames)
			{
				if(calcProperties.contains(field))
				{
					boolean tracking = iDocument.isTrackingChanges();
					if(tracking) iDocument.undo(field);
//					iDocument.removeField(field);
					wasChanged = true;
				}
			}
			return wasChanged?RESULT.RECORD_CHANGED:RESULT.RECORD_NOT_CHANGED;
		}
		
		return RESULT.RECORD_NOT_CHANGED;
	}
	
	/*
	 * Temporal commenting out! It should be fixed in OrientDB. Issue #4158
	 * @Override
	public void onRecordAfterCreate(ODocument iDocument) {
		onRecordAfterRead(iDocument);
	}*/

	@Override
	public void onRecordAfterUpdate(ODocument iDocument) {
		onRecordAfterRead(iDocument);
	}


	@Override
	public void onRecordAfterRead(ODocument document) {
		super.onRecordAfterRead(document);
		OClass oClass = document.getSchemaClass();
		if (oClass != null) {
			List<String> calcProperties = getCalcProperties(document);
			
			if (calcProperties != null && !calcProperties.isEmpty()) {
				propertiesForCalculate(calcProperties, oClass)
						.map(p -> getData(p, document))
						.filter(p -> p.getKey() != null && p.getValue() != null)
						.filter(p -> p.getValue().hasNext())
						.map(this::convertToPropertyValue)
						.forEach(pair -> document.field(pair.getKey().getName(), pair.getValue()));
			}
		}
	}

	private Stream<OProperty> propertiesForCalculate(List<String> properties, OClass oClass) {
		return properties.stream()
				.map(oClass::getProperty)
				.filter(p -> !Strings.isEmpty(CustomAttribute.CALC_SCRIPT.getValue(p)));
	}

	private Pair<OProperty, Object> convertToPropertyValue(Pair<OProperty, OResultSet> pair) {
		OType type = pair.getKey().getType();
		OType linkedType = pair.getKey().getLinkedType();

		Object value;

		if (type.isMultiValue() && linkedType != null) {
			value = OType.convert(convertToPropertyListValue(pair.getValue(), linkedType), type.getDefaultJavaType());
		} else {
			value = OType.convert(pair.getValue().next().getProperty(VALUE), type.getDefaultJavaType());
		}

		return Pair.of(pair.getKey(), value);
	}

	private List<Object> convertToPropertyListValue(OResultSet resultSet, OType linkedType) {
		return resultSet.stream()
				.map(result -> result.getProperty(VALUE))
				.map(value -> OType.convert(value, linkedType.getDefaultJavaType()))
				.collect(Collectors.toCollection(LinkedList::new));
	}

	private Pair<OProperty, OResultSet> getData(OProperty property, ODocument document) {
		String script = CustomAttribute.CALC_SCRIPT.getValue(property);
		try {
			OResultSet data;

			if (FULL_QUERY_PATTERN.matcher(script).find()) {
				data = database.query(script, document);
			} else {
				data = database.query(String.format("select %s as %s from ?", script, VALUE), document);
			}

			return Pair.of(property, data);
		} catch (OCommandSQLParsingException e) {
			LOG.warn("Can't parse SQL for calculable property: {}\nScript: {}", property.getFullName(), script, e);
		}

		return Pair.of(null, null);
	}
}
