package ru.ydn.orienteer.hooks;

import java.util.List;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.schema.SchemaHelper;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

public class CalculablePropertiesHook extends ODocumentHookAbstract
{

	@Override
	public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
		return DISTRIBUTED_EXECUTION_MODE.TARGET_NODE;
	}

	@Override
	public void onRecordAfterRead(ODocument iDocument) {
		super.onRecordAfterRead(iDocument);
		OClass oClass = iDocument.getSchemaClass();
		if(oClass!=null)
		{
			String[] fields = iDocument.fieldNames();
			for (int i = 0; i < fields.length; i++) {
				String field = fields[i];
				if(iDocument.field(field)!=null) continue;
				final OProperty property = oClass.getProperty(field);
				if(SchemaHelper.isPropertyCalculable(property))
				{
					String script = property.getCustom(CustomAttributes.CALC_SCRIPT.getName());
					List<ODocument> calculated = iDocument.getDatabase().query(new OSQLSynchQuery<Object>(script), iDocument);
					if(calculated!=null && calculated.size()>0)
					{
						OType type = property.getType();
						Object value;
						if(type.isMultiValue())
						{
							final OType linkedType = property.getLinkedType();
							value = Lists.transform(calculated, new Function<ODocument, Object>() {

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
						iDocument.field(field, value);
					}
				}
			}
		}
	}
	
	
	
}
