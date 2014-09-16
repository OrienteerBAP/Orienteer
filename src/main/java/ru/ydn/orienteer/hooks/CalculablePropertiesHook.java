package ru.ydn.orienteer.hooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.schema.SchemaHelper;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

public class CalculablePropertiesHook extends ODocumentHookAbstract
{
	private Map<String, Integer> schemaVersions = new ConcurrentHashMap<String, Integer>();
	private Table<String, String, List<String>> calcProperties = HashBasedTable.create();
	
	@Override
	public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
		return DISTRIBUTED_EXECUTION_MODE.TARGET_NODE;
	}
	
	@SuppressWarnings("deprecation")
	private List<String> getCalcProperties(ODocument iDocument)
	{
		ODatabaseRecord db = iDocument.getDatabase();
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
					if(CustomAttributes.CALCULABLE.getValue(property, false))
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
	public void onRecordAfterRead(ODocument iDocument) {
		super.onRecordAfterRead(iDocument);
		OClass oClass = iDocument.getSchemaClass();
		if(oClass!=null)
		{
			List<String> calcProperties = getCalcProperties(iDocument);
			
			if(calcProperties!=null && calcProperties.size()>0)
			{
				for (String calcProperty :calcProperties) {
					if(iDocument.field(calcProperty)!=null) continue;
					final OProperty property = oClass.getProperty(calcProperty);
					String script = CustomAttributes.CALC_SCRIPT.getValue(property);
					List<ODocument> calculated = iDocument.getDatabase().query(new OSQLSynchQuery<Object>(script), iDocument);
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
						iDocument.field(calcProperty, value);
					}
					
				}
				
			}
		}
	}
	
	
	
}
