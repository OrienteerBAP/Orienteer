package org.orienteer.core.dao.handler.extra;

import java.lang.reflect.Method;
import java.util.Optional;

import org.apache.wicket.WicketRuntimeException;
import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.dao.handler.InvocationChain;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

/**
 * Intercepter-like {@link IMethodHandler} to perform method as a super user
 * @param <T> type of target/delegate object
 */
public class SudoMethodHandler<T> implements IMethodHandler<T> {

	@Override
	public Optional<Object> handle(final T target, final Object proxy, final Method method, final Object[] args, final InvocationChain<T> chain)
			throws Throwable {
		return new DBClosure<Optional<Object>>() {

			@Override
			protected Optional<Object> execute(ODatabaseSession db) {
				try {
					if(target instanceof ODocumentWrapper) {
						((ODocumentWrapper) target).reload();
					}
					return chain.handle(target, proxy, method, args);
				} catch (Throwable e) {
					throw new WicketRuntimeException(e);
				}
			}
			
		}.execute();
	}

}
