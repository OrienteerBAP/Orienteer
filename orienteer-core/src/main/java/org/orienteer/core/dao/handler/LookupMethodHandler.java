package org.orienteer.core.dao.handler;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.dao.Lookup;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * {@link IMethodHandler} which load {@link ODocument} into {@link ODocumentWrapper} after lookup it in DB
 */
public class LookupMethodHandler extends AbstractSQLMethodHandler<ODocumentWrapper> {

  @Override
  public ResultHolder handle(ODocumentWrapper target, Object proxy, Method method, Object[] args) throws Throwable {
    if (method.isAnnotationPresent(Lookup.class)) {
      String sql = method.getAnnotation(Lookup.class).value();
      Map<String, Object> arguments = toArguments(method, args);
      ODocument ret = new OSQLSynchQuery<ODocument>(sql).runFirst(arguments);
      target.fromStream(ret);
      return NULL_RESULT;
    }
    return null;
  }

}
