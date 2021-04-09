package org.orienteer.core.dao.handler;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.dao.Lookup;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
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
			ODatabaseSession db = ODatabaseRecordThreadLocal.instance().get();
			Map<String, Object> preparedArgs = toArguments(method, args);
			preparedArgs.put("daoClass", target.getDocument().getClassName());
			try(OResultSet rs =  db.query(sql, preparedArgs)) {
				ODocument ret = null;
				if(rs.hasNext()) {
					ret = (ODocument) rs.next().getRecord().orElse(null);
					target.fromStream(ret);
				}
				return returnChained(proxy, method, ret!=null);
			}
		} else return chain.handle(target, proxy, method, args);
	}

}
