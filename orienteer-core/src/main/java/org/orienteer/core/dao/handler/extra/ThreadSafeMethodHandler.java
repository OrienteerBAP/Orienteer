package org.orienteer.core.dao.handler.extra;

import java.lang.reflect.Method;
import java.util.Optional;

import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.dao.handler.InvocationChain;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;

/**
 * Intercepter-like {@link IMethodHandler} to perform method under current db if present of as a super user
 * @param <T> type of target/delegate object
 */
public class ThreadSafeMethodHandler<T> extends SudoMethodHandler<T>{

	@Override
	public Optional<Object> handle(T target, Object proxy, Method method, Object[] args, InvocationChain<T> chain)
			throws Throwable {
		if(ODatabaseRecordThreadLocal.instance().isDefined()) {
			return chain.handle(target, proxy, method, args);
		} else return super.handle(target, proxy, method, args, chain);
	}

}
