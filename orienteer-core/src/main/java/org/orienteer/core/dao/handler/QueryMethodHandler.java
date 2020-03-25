package org.orienteer.core.dao.handler;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.dao.Query;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * {@link IMethodHandler} to cover methods to query DB
 *
 * @param <T> type of target/delegate object
 */
public class QueryMethodHandler<T> extends AbstractSQLMethodHandler<T> {

  private final SerializableFunction<T, ?> converter;

  public QueryMethodHandler() {
    this(null);
  }

  public QueryMethodHandler(SerializableFunction<T, ? extends Object> converter) {
    this.converter = converter;
  }

  @Override
  public ResultHolder handle(T target, Object proxy, Method method, Object[] args) throws Throwable {
    if (method.isAnnotationPresent(Query.class)) {
      String sql = method.getAnnotation(Query.class).value();
      OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
      Map<String, Object> arguments = createArguments(target, method, args);

      Class<?> returnType = method.getReturnType();
      if (Collection.class.isAssignableFrom(returnType)) {
				return new ResultHolder(query.run(arguments));
			}

      return new ResultHolder(query.runFirst(arguments));
    }

    return null;
  }

  private Map<String, Object> createArguments(T target, Method method, Object[] args) {
		Map<String, Object> result = toArguments(method, args);
		if (converter != null) {
			result.putIfAbsent("target", converter.apply(target));
		}
		return result;
	}

}
