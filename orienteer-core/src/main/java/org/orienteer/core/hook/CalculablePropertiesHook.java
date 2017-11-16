package org.orienteer.core.hook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.apache.wicket.util.string.Strings;
import org.orienteer.core.CustomAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQLParsingException;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * {@link ODocumentHookAbstract} for automatic calculation of some properties.
 * Properties to be automatically calculated should be marked by {@link CustomAttribute}.CALCULABLE
 * Logic for calculation should be stored in {@link CustomAttribute}.CALC_SCRIPT
 */
public class CalculablePropertiesHook extends ODocumentHookAbstract
{
	private static final Logger LOG = LoggerFactory.getLogger(CalculablePropertiesHook.class);
	private final static Pattern FULL_QUERY_PATTERN = Pattern.compile("^\\s*(select|traverse)", Pattern.CASE_INSENSITIVE);
	
	private Map<String, Integer> schemaVersions = new ConcurrentHashMap<String, Integer>();
	private Table<String, String, List<String>> calcProperties = HashBasedTable.create();
	
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
	public void onRecordAfterRead(ODocument iDocument) {
		super.onRecordAfterRead(iDocument);
		OClass oClass = iDocument.getSchemaClass();
		if(oClass!=null)
		{
			List<String> calcProperties = getCalcProperties(iDocument);
			
			if(calcProperties!=null && calcProperties.size()>0)
			{
				for (String calcProperty :calcProperties) {
					//Force calculation. Required for work around issue in OrientDB
					//if(iDocument.field(calcProperty)!=null) continue;
					final OProperty property = oClass.getProperty(calcProperty);
					String script = CustomAttribute.CALC_SCRIPT.getValue(property);
					if(!Strings.isEmpty(script))
					{
						try {
							List<ODocument> calculated;
							if(FULL_QUERY_PATTERN.matcher(script).find())
							{
								calculated = iDocument.getDatabase().query(new OSQLSynchQuery<Object>(script), iDocument);
							}
							else
							{
								script = "select "+script+" as value from "+iDocument.getIdentity();
								calculated = iDocument.getDatabase().query(new OSQLSynchQuery<Object>(script));
							}
							if(calculated!=null && calculated.size()>0)
							{
								OType type = property.getType();
								Object value;
								if(type.isMultiValue())
								{
									final OType linkedType = property.getLinkedType();
									value = linkedType==null
											?calculated
											:Lists.transform(calculated, new Function<ODocument, Object>() {
												
												@Override
												public Object apply(ODocument input) {
													return OType.convert(input.field("value"), linkedType.getDefaultJavaType());
												}
											});
								}
								else
								{
									value = calculated.get(0).field("value");
								}
								value = OType.convert(value, type.getDefaultJavaType());
								Object oldValue = iDocument.field(calcProperty); 
								if (oldValue!=value && (oldValue==null || !oldValue.equals(value))){
									iDocument.field(calcProperty, value);
								}
							}
						} catch (OCommandSQLParsingException e) { //TODO: Refactor because one exception prevent calculation for others
							LOG.warn("Can't parse SQL for calculable property", e);
							iDocument.field(calcProperty, e.getLocalizedMessage());
						}
					}
				}
				
			}
		}
	}
	
	
	
}
