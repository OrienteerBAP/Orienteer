package org.orienteer.core.dao;

import com.google.common.collect.ObjectArrays;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Utility class for creating implementations for required interfaces
 */
public final class DAO {

  private static final Class<?>[] NO_CLASSES = new Class<?>[0];

  private DAO() {

  }

  public static <T> T create(Class<T> interfaceClass, Class<?>... additionalInterfaces) {
    return provide(interfaceClass, new ODocumentWrapper(), additionalInterfaces);
  }

  public static <T> T create(Class<T> interfaceClass, String className, Class<?>... additionalInterfaces) {
    return provide(interfaceClass, new ODocumentWrapper(className), additionalInterfaces);
  }

  public static <T> T provide(Class<T> interfaceClass, ORID iRID, Class<?>... additionalInterfaces) {
    return provide(interfaceClass, new ODocumentWrapper(iRID), additionalInterfaces);
  }

  public static <T> T provide(Class<T> interfaceClass, ODocument doc, Class<?>... additionalInterfaces) {
    return provide(interfaceClass, new ODocumentWrapper(doc), additionalInterfaces);
  }

  @SuppressWarnings("unchecked")
  public static <T> T provide(Class<T> interfaceClass, ODocumentWrapper docWrapper, Class<?>... additionalInterfaces) {
    if (additionalInterfaces == null) {
      additionalInterfaces = NO_CLASSES;
    }
    additionalInterfaces = ObjectArrays.concat(docWrapper.getClass().getInterfaces(), additionalInterfaces, Class.class);

    Class<?>[] interfaces = new Class[additionalInterfaces.length + 2];
    interfaces[0] = interfaceClass;
    interfaces[1] = IODocumentWrapper.class;

    if (additionalInterfaces.length > 0) {
      System.arraycopy(additionalInterfaces, 0, interfaces, 2, additionalInterfaces.length);
    }

    ClassLoader classLoader = interfaceClass.getClassLoader();
    InvocationHandler handler = new ODocumentWrapperInvocationHandler(docWrapper);

    return (T) Proxy.newProxyInstance(classLoader, interfaces, handler);
  }

  @SuppressWarnings("unchecked")
  public static <T> T dao(Class<T> interfaceClass, Class<?>... additionalInterfaces) {
    if (additionalInterfaces == null) {
      additionalInterfaces = NO_CLASSES;
    }

    Class<?>[] interfaces = new Class<?>[additionalInterfaces.length + 1];
    interfaces[0] = interfaceClass;
    if (additionalInterfaces.length > 0) {
      System.arraycopy(additionalInterfaces, 0, interfaces, 1, additionalInterfaces.length);
    }

    ClassLoader classLoader = interfaceClass.getClassLoader();
    InvocationHandler handler = new DAOInvocationHandler();
    return (T) Proxy.newProxyInstance(classLoader, interfaces, handler);
  }
}
