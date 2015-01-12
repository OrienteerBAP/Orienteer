package ru.ydn.orienteer.hooks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ydn.orienteer.CustomAttributes;

import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.db.record.OMultiValueChangeEvent;
import com.orientechnologies.orient.core.db.record.OMultiValueChangeTimeLine;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.hook.ORecordHook.DISTRIBUTED_EXECUTION_MODE;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ReferencesConsistencyHook extends ODocumentHookAbstract
{
	private static final Logger LOG = LoggerFactory.getLogger(ReferencesConsistencyHook.class);
	private int currentSchemaVersion=-1;
	private LoadingCache<OClass, Collection<OProperty>> cache 
								= CacheBuilder.newBuilder().build(new CacheLoader<OClass, Collection<OProperty>>() {

									@Override
									public Collection<OProperty> load(OClass key)
											throws Exception {
										return Collections2.filter(key.properties(), new Predicate<OProperty>() {
												@Override
												public boolean apply(OProperty property) {
													return property.getType().isLink() 
															&& CustomAttributes.PROP_INVERSE.getValue(property)!=null;
												}
											});
									}
								});
	private static final ThreadLocal<Boolean> ENTRY_LOCK = new ThreadLocal<Boolean>()
			{
				@Override
				protected Boolean initialValue() {
					return false;
				}
			};
			
	private boolean enter()
	{
		if(!ENTRY_LOCK.get())
		{
			ENTRY_LOCK.set(true);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private void exit()
	{
		ENTRY_LOCK.set(false);
	}
	
	@Override
	public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
		return DISTRIBUTED_EXECUTION_MODE.TARGET_NODE;
	}
	
	private LoadingCache<OClass, Collection<OProperty>> getCache()
	{
		@SuppressWarnings("deprecation")
		int version = ODatabaseRecordThreadLocal.INSTANCE.get().getMetadata().getSchema().getVersion();
		if(version>currentSchemaVersion)
		{
			cache.invalidateAll();
			currentSchemaVersion=version;
		}
		return cache;
	}

	@Override
	public void onRecordAfterCreate(ODocument doc) {
		if(enter())
		{
			try
			{
				OClass thisOClass = doc.getSchemaClass();
				if(thisOClass==null) return;
				Collection<OProperty> refProperties = getCache().get(thisOClass);
				for (OProperty oProperty : refProperties)
				{
					OProperty inverseProperty = CustomAttributes.PROP_INVERSE.getValue(oProperty);
					Object value = doc.field(oProperty.getName());
					if(value instanceof OIdentifiable) value = Arrays.asList(value);
					if(inverseProperty!=null && value!=null && value instanceof Collection)
					{
						for(Object otherObj: (Collection<?>)value)
						{
							if(otherObj instanceof OIdentifiable)
							{
								ODocument otherDoc = ((OIdentifiable) otherObj).getRecord();
								addLink(otherDoc, inverseProperty, doc);
							}
						}
					}
				}
			} catch (ExecutionException e)
			{
				LOG.error("Can't update reverse links onCreate", e);
			}
			finally
			{
				exit();
			}
		}
	}
	
	

	@Override
	public void onRecordAfterUpdate(ODocument doc) {
		if(enter())
		{
			try
			{
				OClass thisOClass = doc.getSchemaClass();
				if(thisOClass==null) return;
				Collection<OProperty> refProperties = getCache().get(thisOClass);
				if(refProperties!=null && refProperties.size()>0)
				{
					String[] changedFields = doc.getDirtyFields();
					for (String field : changedFields)
					{
						OProperty changedProperty = thisOClass.getProperty(field);
						if(refProperties.contains(changedProperty))
						{
							OProperty inverseProperty = CustomAttributes.PROP_INVERSE.getValue(changedProperty);
							if(changedProperty.getType().isMultiValue())
							{
								OMultiValueChangeTimeLine<Object, Object> timeline = doc.getCollectionTimeLine(field);
								List<OMultiValueChangeEvent<Object, Object>> events = timeline.getMultiValueChangeEvents();
								for (OMultiValueChangeEvent<Object, Object> event : events)
								{
									OIdentifiable toAddTo=null;
									OIdentifiable toRemoveFrom=null;
									switch (event.getChangeType())
									{
										case ADD:
											toAddTo = (OIdentifiable)event.getValue();
											break;
										case UPDATE:
											toAddTo = (OIdentifiable)event.getValue();
											toRemoveFrom = (OIdentifiable)event.getOldValue();
											break;
										case REMOVE:
											toRemoveFrom = (OIdentifiable)event.getOldValue();
											break;
									}
									if(toAddTo!=null) addLink((ODocument)toAddTo.getRecord(), inverseProperty, doc);
									if(toRemoveFrom!=null) removeLink((ODocument)toRemoveFrom.getRecord(), inverseProperty, doc);
								}
							}
							else
							{
								Object original = doc.getOriginalValue(field);
								Object current = doc.field(field);
								if(original!=null && original instanceof OIdentifiable) 
									removeLink((ODocument)((OIdentifiable)original).getRecord(), inverseProperty, doc);
								if(current!=null && current instanceof OIdentifiable)
									addLink((ODocument)((OIdentifiable)current).getRecord(), inverseProperty, doc);
							}
						}
					}
				}
				
			} catch (ExecutionException e)
			{
				LOG.error("Can't update reverse links onUpdate", e);
			}
			finally
			{
				exit();
			}
		}
	}

	@Override
	public void onRecordAfterDelete(ODocument doc) {
		if(enter())
		{
			try
			{
				OClass thisOClass = doc.getSchemaClass();
				if(thisOClass==null) return;
				Collection<OProperty> refProperties = getCache().get(thisOClass);
				for (OProperty oProperty : refProperties)
				{
					OProperty inverseProperty = CustomAttributes.PROP_INVERSE.getValue(oProperty);
					Object value = doc.field(oProperty.getName());
					if(value instanceof OIdentifiable) value = Arrays.asList(value);
					if(inverseProperty!=null && value!=null && value instanceof Collection)
					{
						for(Object otherObj: (Collection<?>)value)
						{
							if(otherObj instanceof OIdentifiable)
							{
								ODocument otherDoc = ((OIdentifiable) otherObj).getRecord();
								removeLink(otherDoc, inverseProperty, doc);
							}
						}
					}
				}
			} catch (ExecutionException e)
			{
				LOG.error("Can't update reverse links onDelete", e);
			}
			finally
			{
				exit();
			}
		}
	}
	
	private void addLink(ODocument doc, OProperty property, ODocument value)
	{
		if(doc==null || property ==null || value == null) return;
		String field = property.getName();
		if(doc.getSchemaClass().isSubClassOf(property.getOwnerClass()))
		{
			Object wrappedValue = value.getIdentity().isPersistent()?value.getIdentity():value;
			if(property.getType().isMultiValue())
			{
				Collection<Object> objects = doc.field(field);
				if(objects==null)
				{
					objects = new ArrayList<Object>();
					objects.add(wrappedValue);
					doc.field(field, objects);
				}
				else
				{
					objects.add(wrappedValue);
				}
			}
			else
			{
				doc.field(field, wrappedValue);
			}
			doc.save();
		}
	}
	
	private void removeLink(ODocument doc, OProperty property, ODocument value)
	{
		if(doc==null || property ==null || value == null) return;
		String field = property.getName();
		if(doc.getSchemaClass().isSubClassOf(property.getOwnerClass()))
		{
			Object wrappedValue = value.getIdentity().isPersistent()?value.getIdentity():value;
			if(property.getType().isMultiValue())
			{
				Collection<Object> objects = doc.field(field);
				if(objects!=null && objects.remove(wrappedValue))
				{
					doc.field(field, objects);
					doc.save();
				}
			}
			else
			{
				if(value.getIdentity().equals(doc.field(field, ORID.class)))
				{
					doc.field(field, (Object) null);
					doc.save();
				}
			}
		}
	}
	
}
