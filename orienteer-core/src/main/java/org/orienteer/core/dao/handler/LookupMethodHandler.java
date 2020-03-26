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
public class LookupMethodHandler extends AbstractMethodHandler<ODocumentWrapper> {

  @Override
  public ResultHolder handle(ODocumentWrapper target, Object proxy, Method method, Object[] args) throws Throwable {
    if (method.isAnnotationPresent(Lookup.class)) {
      String sql = method.getAnnotation(Lookup.class).value();
      ODocument ret = new OSQLSynchQuery<ODocument>(sql).runFirst(toArguments(method, args));
      target.fromStream(ret);
      return returnChained(proxy, method, ret != null);
    }

    return null;
  }

}
