package org.orienteer.core.dao.handler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.dao.Lookup;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * {@link IMethodHandler} which load {@link ODocument} into {@link ODocumentWrapper} after lookup it in DB
 */
public class LookupMethodHandler extends AbstractMethodHandler<ODocumentWrapper> {

	@Override
	public Optional<Object> handle(ODocumentWrapper target, Object proxy, Method method, Object[] args, InvocationChain<ODocumentWrapper> chain) throws Throwable {
		if(method.isAnnotationPresent(Lookup.class)) {
			String sql = method.getAnnotation(Lookup.class).value();
			ODocument ret = new OSQLSynchQuery<ODocument>(sql).runFirst(toArguments(method, args));
			target.fromStream(ret);
			return returnChained(proxy, method, ret!=null);
		} else return chain.handle(target, proxy, method, args);
	}

}
