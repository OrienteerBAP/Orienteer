package org.orienteer.core.dao;

import org.apache.wicket.util.lang.Args;
import org.orienteer.transponder.Transponder;
import org.orienteer.transponder.orientdb.IODocumentWrapper;
import org.orienteer.transponder.orientdb.ODriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * Utility class for creating implementations for required interfaces
 */
public final class DAO {
	
	public static final Transponder TRANSPONDER = new Transponder(new OrienteerDriver(true));
	
	private DAO() {
		
	}
	
	public static ODocumentWrapper asWrapper(Object obj) {
		if(obj==null) return null;
		else if (obj instanceof ODocumentWrapper) return (ODocumentWrapper)obj;
		else throw new IllegalStateException("Object is not a wrapper. Object: "+obj);
	}
	
	public static ODocument asDocument(Object obj) {
		return obj!=null?asWrapper(obj).getDocument():null;
	}
	
	public static <T> T as(Object proxy, Class<? extends T> clazz) {
		return clazz.cast(proxy);
	}
	
	public static <T> T loadFromDocument(T obj, ODocument doc) {
		asWrapper(obj).fromStream(doc);
		return obj;
	}
	
	public static <T> T save(T obj) {
		return (T) asWrapper(obj).save();
	}
	
	public static <T> T reload(T obj) {
		return (T) asWrapper(obj).reload();
	}
	
	public static <T> T create(Class<T> interfaceClass, Class<?>... additionalInterfaces) {
		return TRANSPONDER.create(interfaceClass, additionalInterfaces);
	}
	
	public static <T> T create(Class<T> interfaceClass, String className, Class<?>... additionalInterfaces) {
		return TRANSPONDER.create(interfaceClass, className, additionalInterfaces);
	}
	
	public static <T> T provide(Class<T> interfaceClass, ORID iRID, Class<?>... additionalInterfaces) {
		if(iRID==null) throw new NullPointerException("ORID for DAO.provide(...) should not be null");
		return provide(interfaceClass, (OIdentifiable)iRID, additionalInterfaces);
	}
	
	public static <T> T provide(Class<T> interfaceClass, ODocument doc, Class<?>... additionalInterfaces) {
		if(doc==null) throw new NullPointerException("Document for DAO.provide(...) should not be null");
		return provide(interfaceClass, (OIdentifiable)doc, additionalInterfaces);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T provide(Class<? extends T> interfaceClass, OIdentifiable id, Class<?>... additionalInterfaces) {
		Args.notNull(ODatabaseRecordThreadLocal.instance().get(), "There is no DatabaseSession");
		return TRANSPONDER.provide(id, interfaceClass, additionalInterfaces);
	}
	
	public static boolean compatible(Object proxy, Class<?>... interfaces) {
		for (Class<?> inter : interfaces) {
			if(!inter.isInstance(proxy)) return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T dao(Class<T> interfaceClass, Class<?>... additionalInterfaces) {
		return TRANSPONDER.dao(interfaceClass, additionalInterfaces);
	}
	
	public static void define(Class<?>...classes) {
		TRANSPONDER.define(classes);
	}
	
}