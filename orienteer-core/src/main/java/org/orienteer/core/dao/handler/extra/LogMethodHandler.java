package org.orienteer.core.dao.handler.extra;

import java.lang.reflect.Method;
import java.util.Optional;

import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.dao.handler.InvocationChain;

import lombok.extern.slf4j.Slf4j;

/**
 * Intercepter-like {@link IMethodHandler} to log current invocation
 * @param <T> type of target/delegate object
 */
@Slf4j
public class LogMethodHandler<T> implements IMethodHandler<T> {

	@Override
	public Optional<Object> handle(T target, Object proxy, Method method, Object[] args, InvocationChain<T> chain)
			throws Throwable {
		StringBuilder sb = new StringBuilder("Method ");
		sb.append(method).append(" invoked for ").append(target);
		if(args!=null && args.length>0) {
			sb.append(" with arguments:\n");
			for (int i = 0; i < args.length; i++) {
				sb.append(i).append(":> ").append(args[i]);
				if(i<args.length-1) sb.append("\n");
			}
		}
		log.info(sb.toString());
		long start = System.currentTimeMillis();
		Optional<Object> ret = null;
		try {
			ret = chain.handle(target, proxy, method, args);
			sb.setLength(0);
			sb.append("Method ").append(method).append(" returned the following result (")
							.append(System.currentTimeMillis()-start).append("ms):\n")
							.append(ret!=null?ret.orElse(null):"!NO RESULT!");
			log.info(sb.toString());
			return ret;
		} catch(Throwable th) {
			sb.append("Method ").append(method).append(" throwed Exception");
			log.info(sb.toString(), th);
			throw th;
		}
	}

}
