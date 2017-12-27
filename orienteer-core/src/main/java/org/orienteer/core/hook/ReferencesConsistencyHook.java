package org.orienteer.core.hook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.orienteer.core.CustomAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
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

/**
 * {@link ODocumentHookAbstract} for keeping references consistency between documents
 */
public class ReferencesConsistencyHook extends ODocumentHookAbstract
{
	private static final Logger LOG = LoggerFactory.getLogger(ReferencesConsistencyHook.class);
	private int currentSchemaVersion=-1;
	private static final LoadingCache<OClass, Collection<OProperty>> CACHE 
								= CacheBuilder.newBuilder().build(new CacheLoader<OClass, Collection<OProperty>>() {

									@Override
									public Collection<OProperty> load(OClass key)
											throws Exception {
										return Collections2.filter(key.properties(), new Predicate<OProperty>() {
												@Override
												public boolean apply(OProperty property) {
													return property.getType().isLink() 
															&& CustomAttribute.PROP_INVERSE.getValue(property)!=null;
												}
											});
									}
								});
	private static final ThreadLocal<List<ODocument>> ENTRY_LOCK = new ThreadLocal<List<ODocument>>()
			{
				@Override
				protected List<ODocument> initialValue() {
					return new ArrayList<ODocument>(3);
				}
			};
	private static final ThreadLocal<Boolean> HOOK_DISABLED = new ThreadLocal<Boolean>()
			{
				@Override
				protected Boolean initialValue() {
					return false;
				}
			};
			
	public ReferencesConsistencyHook(ODatabaseDocument database) {
		super(database);
	}

	private boolean enter(ODocument doc)
	{
		if(doc.getSchemaClass()==null || HOOK_DISABLED.get()) return false;
		List<ODocument> docs = ENTRY_LOCK.get();
		boolean ret = !docs.contains(doc);
		if(ret) docs.add(doc);
		return ret;
	}
	
	private void exit(ODocument doc)
	{
		ENTRY_LOCK.get().remove(doc);
	}
	
	private boolean isUnderTheLock(ODocument doc)
	{
		return ENTRY_LOCK.get().contains(doc);
	}
	
	private void saveOutOfHook(ODocument doc)
	{
		try
		{
			HOOK_DISABLED.set(true);
			doc.save();
		}
		finally
		{
			HOOK_DISABLED.set(false);
		}
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
			CACHE.invalidateAll();
			currentSchemaVersion=version;
		}
		return CACHE;
	}

	@Override
	public void onRecordAfterCreate(ODocument doc) {
		if(enter(doc))
		{
			try
			{
				OClass thisOClass = doc.getSchemaClass();
//				if(thisOClass==null) return;
				Collection<OProperty> refProperties = getCache().get(doc.getSchemaClass());
				for (OProperty oProperty : refProperties)
				{
					OProperty inverseProperty = CustomAttribute.PROP_INVERSE.getValue(oProperty);
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
				exit(doc);
			}
		}
	}
	
	

	@Override
	public void onRecordAfterUpdate(ODocument doc) {
		if(enter(doc))
		{
			try
			{
				OClass thisOClass = doc.getSchemaClass();
//				if(thisOClass==null) return;
				Collection<OProperty> refProperties = getCache().get(thisOClass);
				if(refProperties!=null && refProperties.size()>0)
				{
					String[] changedFields = doc.getDirtyFields();
					for (String field : changedFields)
					{
						OProperty changedProperty = thisOClass.getProperty(field);
						if(refProperties.contains(changedProperty))
						{
							OProperty inverseProperty = CustomAttribute.PROP_INVERSE.getValue(changedProperty);
							if(changedProperty.getType().isMultiValue())
							{
								OMultiValueChangeTimeLine<Object, Object> timeline = doc.getCollectionTimeLine(field);
								if(timeline!=null)
								{
									//Our old collection was modified, so we can perform changes one by one
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
									//whole collection was replaces
									Object original = doc.getOriginalValue(field);
									Object current = doc.field(field);
									if(original!=null && original instanceof Iterable)
									{
										for(Object originaIdentifiable: (Iterable<?>)original)
										{
											if(originaIdentifiable!=null && originaIdentifiable instanceof OIdentifiable) 
												removeLink((ODocument)((OIdentifiable)originaIdentifiable).getRecord(), 
															inverseProperty, doc);
										}
									}
									if(current!=null && current instanceof Iterable)
									{
										for(Object currentIdentifiable: (Iterable<?>)current)
										{
											if(currentIdentifiable!=null && currentIdentifiable instanceof OIdentifiable) 
												addLink((ODocument)((OIdentifiable)currentIdentifiable).getRecord(),
															inverseProperty, doc);
										}
									}
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
				exit(doc);
			}
		}
	}

	@Override
	public void onRecordAfterDelete(ODocument doc) {
		if(enter(doc))
		{
			try
			{
				OClass thisOClass = doc.getSchemaClass();
//				if(thisOClass==null) return;
				Collection<OProperty> refProperties = getCache().get(thisOClass);
				for (OProperty oProperty : refProperties)
				{
					OProperty inverseProperty = CustomAttribute.PROP_INVERSE.getValue(oProperty);
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
				exit(doc);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addLink(ODocument doc, OProperty property, ODocument value)
	{
		if(doc==null || property ==null || value == null || isUnderTheLock(doc)) return;
		String field = property.getName();
		if(doc.getSchemaClass().isSubClassOf(property.getOwnerClass()))
		{
			Object wrappedValue = value.getIdentity().isPersistent()?value.getIdentity():value;
			Object oldValue = doc.field(field);
			if(property.getType().isMultiValue())
			{
				Collection<Object> objects = (Collection<Object>) oldValue;
				if(objects==null)
				{
					objects = new ArrayList<Object>(1);
					objects.add(wrappedValue);
					doc.field(field, objects);
					//It's safe of fields with multivalue
					saveOutOfHook(doc);
				}
				else if(!objects.contains(wrappedValue)) 
				{
					objects.add(wrappedValue);
					//It's safe of fields with multivalue
					saveOutOfHook(doc);
				}
			}
			else
			{
				if (oldValue==null || !oldValue.equals(wrappedValue)){
					doc.field(field, wrappedValue);
					doc.save();
				}
			}
		}
	}
	
	private void removeLink(ODocument doc, OProperty property, ODocument value)
	{
		if(doc==null || property ==null || value == null || isUnderTheLock(doc)) return;
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
					//It's safe for multivalue docs
					saveOutOfHook(doc);
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
