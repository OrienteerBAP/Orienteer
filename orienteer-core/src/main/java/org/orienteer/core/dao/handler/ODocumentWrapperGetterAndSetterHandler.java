package org.orienteer.core.dao.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.util.CommonUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * {@link IMethodHandler} to GET and SET field value to {@link ODocumentWrapper}
 */
public class ODocumentWrapperGetterAndSetterHandler implements IMethodHandler<ODocumentWrapper> {

  private static final List<ODocumentFieldHandler> GETTER_AND_SETTERS = Arrays.asList(
          new NumberHandler()
  );

  @Override
  public ResultHolder handle(ODocumentWrapper target, Object proxy, Method method, Object[] args) throws Throwable {
    if (isGet(method, args) || isSet(method, args)) {
      String fieldName = CommonUtils.decapitalize(method.getName().substring(3));
      ODocument document = target.getDocument();
      if (isGet(method, args)) {
        Object result = getHandler(method.getReturnType()).get(document, fieldName, method.getReturnType());
        return new ResultHolder(result);
      } else {
        getHandler(method.getParameterTypes()[0]).set(document, fieldName, args[0]);
        return NULL_RESULT;
      }
    }

    return null;
  }

  private ODocumentFieldHandler getHandler(Class<?> type) {
    return findDocumentFieldHandler(type)
            .orElseThrow(() -> new IllegalStateException("There is no supported handler for return type: " + type));
  }

  private boolean isGet(Method method, Object[] args) {
    return method.getName().startsWith("get") && args.length == 0;
  }

  private boolean isSet(Method method, Object[] args) {
    return method.getName().startsWith("set") && args.length == 1;
  }

  protected Optional<ODocumentFieldHandler> findDocumentFieldHandler(Class<?> targetType) {
    return GETTER_AND_SETTERS.stream()
            .filter(handler -> handler.isSupported(targetType))
            .findFirst();
  }


  protected interface ODocumentFieldHandler {

    Object get(ODocument document, String field, Class<?> type);
    void set(ODocument document, String field, Object value);

    boolean isSupported(Class<?> type);
  }

  protected static abstract class AbstractDocumentFieldHandler implements ODocumentFieldHandler {

    protected final Class<?>[] supportedTypes;

    public AbstractDocumentFieldHandler(OType...supportedTypes) {
      this(toDefaultJavaClasses(supportedTypes));
    }

    public AbstractDocumentFieldHandler(Class<?>...supportedTypes) {
      this.supportedTypes = supportedTypes;
    }

    private static Class<?> [] toDefaultJavaClasses(OType...types) {
      Class<?>[] result = new Class<?>[types.length];
      for (int i = 0; i < types.length; i++) {
        result[i] = types[i].getDefaultJavaType();
      }
      return result;
    }

    @Override
    public boolean isSupported(Class<?> type) {
      for (Class<?> supportedType : supportedTypes) {
        if (supportedType.equals(type) || supportedType.isAssignableFrom(type)) {
          return true;
        }
      }
      return false;
    }

  }

  protected static abstract class PureSetter extends AbstractDocumentFieldHandler {

    public PureSetter(OType... supportedTypes) {
      super(supportedTypes);
    }

    public PureSetter(Class<?>... supportedTypes) {
      super(supportedTypes);
    }

    @Override
    public void set(ODocument document, String field, Object value) {
      document.field(field, value);
    }

  }

  protected static class NumberHandler extends PureSetter {

    public NumberHandler() {
      super(
              OType.INTEGER.getDefaultJavaType(), int.class,
              OType.SHORT.getDefaultJavaType(), short.class,
              OType.LONG.getDefaultJavaType(), long.class,
              OType.FLOAT.getDefaultJavaType(), float.class,
              OType.DOUBLE.getDefaultJavaType(), double.class,
              OType.BYTE.getDefaultJavaType(), byte.class
      );
    }

    @Override
    public Object get(ODocument document, String field, Class<?> type) {
      Number number = document.field(field);

      if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
        return number != null ? number.intValue() : 0;
      }
      if (Short.class.isAssignableFrom(type) || short.class.isAssignableFrom(type)) {
        return number != null ? number.shortValue() : (short) 0;
      }
      if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)) {
        return number != null ? number.longValue() : (long) 0;
      }
      if (Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type)) {
        return number != null ? number.floatValue() : (float) 0;
      }
      if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)) {
        return number != null ? number.doubleValue() : (double) 0;
      }
      if (Byte.class.isAssignableFrom(type) || byte.class.isAssignableFrom(type)) {
        return number != null ? number.byteValue() : (byte) 0;
      }

      throw new IllegalStateException("Can't handle number of field '" + field
              + "', field class: " + number.getClass().getSimpleName() + ", field value: " + number);
    }

  }

}
