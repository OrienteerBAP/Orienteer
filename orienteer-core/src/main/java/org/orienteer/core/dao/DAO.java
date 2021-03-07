package org.orienteer.core.dao;

//import static com.google.common.primitives.Primitives.wrap;
import static org.orienteer.core.dao.handler.AbstractMethodHandler.typeToRequiredClass;
import static org.orienteer.core.util.CommonUtils.decapitalize;
import static org.orienteer.core.util.CommonUtils.toMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.Strings;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.core.util.OSchemaHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * Utility class for creating implementations for required interfaces
 */
public final class DAO {
	
	private static final Logger LOG = LoggerFactory.getLogger(DAO.class);
	
	private static final Class<?>[] NO_CLASSES = new Class<?>[0];
	
	private static final Map<OType, OType> EMBEDDED_TO_LINKS_MAP = toMap(OType.EMBEDDED, OType.LINK,
																		 OType.EMBEDDEDLIST, OType.LINKLIST,
																		 OType.EMBEDDEDSET, OType.LINKSET,
																		 OType.EMBEDDEDMAP, OType.LINKMAP);
	
	private static final DAOField DEFAULT_DAOFIELD = dao(DAOField.class);
	
	private DAO() {
		
	}
	
	public static IODocumentWrapper asWrapper(Object obj) {
		if(obj==null) return null;
		else if (obj instanceof IODocumentWrapper) return (IODocumentWrapper)obj;
		else throw new IllegalStateException("Object is not a wrapper. Object: "+obj);
	}
	
	public static ODocument asDocument(Object obj) {
		return obj!=null?asWrapper(obj).getDocument():null;
	}
	
	public static <T> T as(Object proxy, Class<? extends T> clazz) {
		return clazz.cast(proxy);
	}
	
	public static <T> T loadFromDocument(T obj, ODocument doc) {
		asWrapper(obj).fromStream(doc);
		return obj;
	}
	
	public static <T> T save(T obj) {
		return asWrapper(obj).save();
	}
	
	public static <T> T reload(T obj) {
		return asWrapper(obj).reload();
	}
	
	public static <T> T create(Class<T> interfaceClass, Class<?>... additionalInterfaces) {
		DAOOClass daoOClass = interfaceClass.getAnnotation(DAOOClass.class);
		ODocumentWrapper docWrapper = daoOClass!=null && !Strings.isEmpty(daoOClass.value())
											? new ODocumentWrapper(daoOClass.value())
											: new ODocumentWrapper();
		return provide(interfaceClass, docWrapper, additionalInterfaces);
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
	public static <T> T provide(Class<? extends T> interfaceClass, ODocumentWrapper docWrapper, Class<?>... additionalInterfaces) {
		Args.notNull(ODatabaseRecordThreadLocal.instance().get(), "There is no DatabaseSession");
		if(additionalInterfaces == null) additionalInterfaces = NO_CLASSES;
		Class<?>[] builtInInterfaces = docWrapper.getClass().getInterfaces();
		Class<?>[] interfaces = new Class[2+builtInInterfaces.length+additionalInterfaces.length];
		ClassLoader classLoader = interfaceClass.getClassLoader();
		interfaces[0] = tryToGetInheritedInterface(interfaceClass, docWrapper);
		interfaces[1] = IODocumentWrapper.class;
		if(builtInInterfaces.length>0) System.arraycopy(builtInInterfaces, 0, interfaces, 2, builtInInterfaces.length);
		if(additionalInterfaces.length>0) System.arraycopy(additionalInterfaces, 0, interfaces, 2+builtInInterfaces.length, additionalInterfaces.length);
		return (T) Proxy.newProxyInstance(classLoader, interfaces,  new ODocumentWrapperInvocationHandler(docWrapper));
	}
	
	public static <T> T updateBy(T proxy, Class<?>... additionalInterfaces) {
		if(additionalInterfaces==null 
				|| additionalInterfaces.length==0 
				|| compatible(proxy, additionalInterfaces)) return (T) proxy;
		else {
			ClassLoader classLoader = additionalInterfaces[0].getClassLoader();
			InvocationHandler invocationHandler = Proxy.getInvocationHandler(proxy);
			Set<Class<?>> interfaces = new HashSet<Class<?>>();
			interfaces.addAll(Arrays.asList(additionalInterfaces));
			interfaces.addAll(Arrays.asList(proxy.getClass().getInterfaces()));
			return (T) Proxy.newProxyInstance(classLoader, interfaces.toArray(new Class[interfaces.size()]),  invocationHandler);
		}
	}
	
	public static <T> T upgradeTo(Object proxy, Class<? extends T> interfaceClass, Class<?>... additionalInterfaces) {
		if(compatible(proxy, interfaceClass) && compatible(proxy, additionalInterfaces)) return (T) proxy;
		else {
			ClassLoader classLoader = interfaceClass.getClassLoader();
			InvocationHandler invocationHandler = Proxy.getInvocationHandler(proxy);
			Set<Class<?>> interfaces = new HashSet<Class<?>>();
			interfaces.add(interfaceClass);
			interfaces.addAll(Arrays.asList(additionalInterfaces));
			interfaces.addAll(Arrays.asList(proxy.getClass().getInterfaces()));
			return (T) Proxy.newProxyInstance(classLoader, interfaces.toArray(new Class[interfaces.size()]),  invocationHandler);
		}
	}
	
	public static boolean compatible(Object proxy, Class<?>... interfaces) {
		for (Class<?> inter : interfaces) {
			if(!inter.isInstance(proxy)) return false;
		}
		return true;
	}
	
	private static <T> Class<? extends T> tryToGetInheritedInterface(Class<? extends T> clazz, ODocumentWrapper docWrapper) {
		ODocument doc = docWrapper.getDocument();
		if(doc!=null) {
			String daoClassName = CustomAttribute.DAO_CLASS.getValue(doc.getSchemaClass());
			if(daoClassName!=null) {
				try {
					Class<? extends T> daoClass = (Class<? extends T>)Class.forName(daoClassName);
					if(clazz.isAssignableFrom(daoClass)) return daoClass;
				} catch (ClassNotFoundException e) {
					//NOP
				}
			}
		}
		return clazz;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T dao(Class<T> interfaceClass, Class<?>... additionalInterfaces) {
		if(additionalInterfaces == null) additionalInterfaces = NO_CLASSES;
		Class<?>[] interfaces = new Class[1+additionalInterfaces.length];
		interfaces[0] = interfaceClass;
		if(additionalInterfaces.length>0) System.arraycopy(additionalInterfaces, 0, interfaces, 1, additionalInterfaces.length);
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), interfaces ,  new DAOInvocationHandler());
	}
	
	
	public static OSchemaHelper describe(OSchemaHelper helper, Class<?>... classes) {
		DescribeContext ctx = new DescribeContext();
		describe(helper, Arrays.asList(classes), ctx);
		ctx.close(false);
		return helper;
	}
	
	private static Set<String> describe(OSchemaHelper helper, List<Class<?>> classes, DescribeContext ctx) {
		Set<String> oClassNames = new HashSet<String>();
		for (Class<?> clazz : classes) {
			String className = describe(helper, clazz, ctx);
			if(className!=null) oClassNames.add(className);
		}
		return oClassNames;
	}
	
	static String describe(OSchemaHelper helper, Class<?> clazz, DescribeContext ctx) {
		if(clazz==null || !clazz.isInterface()) return null;	
		DAOOClass daooClass = clazz.getAnnotation(DAOOClass.class);
		if(daooClass==null) return null;
		if(ctx.wasDescribed(clazz)) return ctx.getOClass(clazz);
		ctx.entering(clazz, daooClass.value());
		List<Class<?>> interfaces = Arrays.asList(clazz.getInterfaces());
		Set<String> superClasses = describe(helper, interfaces, ctx);
		superClasses.addAll(Arrays.asList(daooClass.superClasses()));
		
		int orderOffset = daooClass.orderOffset();
		int currentOrder=0;
		
		List<Method> methods = listMethods(clazz);
		
		for(Method method : methods) {
			if(method.isDefault() || Modifier.isStatic(method.getModifiers())) continue; //Ignore default methods
			String methodName = method.getName();
			Parameter[] params =  method.getParameters();
			String fieldNameCandidate = null;
			final Class<?> javaType;
			Class<?> subJavaTypeCandidate = null;
			if(methodName.startsWith("set") && params.length==1) {
				fieldNameCandidate = decapitalize(methodName.substring(3));
				javaType = params[0].getType();
				subJavaTypeCandidate = typeToRequiredClass(params[0].getParameterizedType(), javaType);
			} else if(methodName.startsWith("get") && params.length==0) {
				fieldNameCandidate = decapitalize(methodName.substring(3));
				javaType = method.getReturnType();
				subJavaTypeCandidate = typeToRequiredClass(method.getGenericReturnType(), javaType);
			} else if(methodName.startsWith("is") && params.length==0) {
				fieldNameCandidate = decapitalize(methodName.substring(2));
				javaType = method.getReturnType();
				subJavaTypeCandidate = typeToRequiredClass(method.getGenericReturnType(), javaType);
			} else continue;
			if(subJavaTypeCandidate!=null && subJavaTypeCandidate.equals(javaType)) subJavaTypeCandidate = null;
			final Class<?> subJavaType = subJavaTypeCandidate;
			final DAOField daoField = method.getAnnotation(DAOField.class);
			if(daoField!=null && !Strings.isEmpty(daoField.value())) fieldNameCandidate = daoField.value();
			final boolean wasPreviouslyScheduled = ctx.isPropertyCreationScheduled(fieldNameCandidate);
			//Skip second+ attempt to create a property, except if @DAOField is present
			if(wasPreviouslyScheduled && canSkipIfAlreadyScheduled(daoField)) continue;
			OType oTypeCandidate = daoField!=null && !OType.ANY.equals(daoField.type())
											?daoField.type()
											:getTypeByClass(javaType);
			OType linkedTypeCandidate = daoField!=null && !OType.ANY.equals(daoField.linkedType())
											?daoField.linkedType()
											:(subJavaType!=null?getTypeByClass(subJavaType):null);
			final int order = daoField!=null && daoField.order()>=0
									?daoField.order()
									:(orderOffset+10*currentOrder++);
			String linkedClassCandidate = ctx.resolveOrDescribeOClass(helper, javaType);
			if(linkedClassCandidate==null) linkedClassCandidate = ctx.resolveOrDescribeOClass(helper, subJavaType);
			/*String linkedClassCandidate = ctx.resolveOrDescribeOClass(helper, subJavaType);
			if(linkedClassCandidate==null) linkedClassCandidate = ctx.resolveOrDescribeOClass(helper, javaType);*/
			if(linkedClassCandidate==null && daoField!=null && !Strings.isEmpty(daoField.linkedClass())) linkedClassCandidate = daoField.linkedClass();
			if(oTypeCandidate==null && linkedClassCandidate!=null) {
				oTypeCandidate = OType.EMBEDDED;
			}
			if(linkedClassCandidate!=null && EMBEDDED_TO_LINKS_MAP.containsKey(oTypeCandidate) &&(daoField==null || !daoField.embedded())) {
				oTypeCandidate = EMBEDDED_TO_LINKS_MAP.get(oTypeCandidate);
			}
			
			final String fieldName = fieldNameCandidate;
			final OType oType = oTypeCandidate!=null?oTypeCandidate:OType.ANY;
			final String linkedClass = linkedClassCandidate;
			final OType linkedType = linkedClass==null?linkedTypeCandidate:null;
			final boolean notNull = javaType.isPrimitive() || (daoField!=null && daoField.notNull());
			final DAOFieldIndex fieldIndex = method.getAnnotation(DAOFieldIndex.class);
			LOG.info("Method: {} OCLass: {} Field: {} Type: {} LinkedType: {} LinkedClass: {}",methodName, daooClass.value(), fieldName, oType, linkedType, linkedClass);
			
			ctx.postponeTillExit(fieldName, () -> {
				LOG.info("Create property {} ({}) order {}. LinkedType: {}", fieldName, oType, order, linkedType);
				helper.oProperty(fieldName, oType, order);
				if(linkedType!=null) helper.linkedType(linkedType);
				helper.notNull(notNull);
				applyDAOFieldAttribute(helper, daoField);
				if(fieldIndex!=null) helper.oIndex(fieldIndex.name(), fieldIndex.type());
			});
			if(linkedClass!=null && !wasPreviouslyScheduled) ctx.postponeTillDefined(linkedClass, () -> {
				String inverse = daoField!=null?daoField.inverse():null;
				if(Strings.isEmpty(inverse)) {
					LOG.info("Setup relationship {}.{} -> {}", daooClass.value(), fieldName, linkedClass);
					helper.setupRelationship(daooClass.value(), fieldName, linkedClass);
				} else {
					LOG.info("Setup relationship {}.{} -> {} ({})", daooClass.value(), fieldName, linkedClass, inverse);
					helper.setupRelationship(daooClass.value(), fieldName, linkedClass, inverse); 
				}
			});
		}
		
		LOG.info("Creation of OClass {}", daooClass.value());
		if(daooClass.isAbstract()) helper.oAbstractClass(daooClass.value(), superClasses.toArray(new String[superClasses.size()]));
		else helper.oClass(daooClass.value(), superClasses.toArray(new String[superClasses.size()]));
		CustomAttribute.DAO_CLASS.setValue(helper.getOClass(), clazz.getName());
		
		ctx.exiting(clazz, daooClass.value());
		applyDAOClassAttributes(helper, daooClass);
		for (DAOIndex index : clazz.getAnnotationsByType(DAOIndex.class)) {
			helper.oIndex(index.name(), index.type(), index.fields());
		}
		LOG.info("End of Creation of OClass {}", daooClass.value());
		return daooClass.value();
	}
	
	private static boolean canSkipIfAlreadyScheduled(DAOField daoField) {
		if(daoField==null) return true;
		Set<String> difference = CommonUtils.diffAnnotations(daoField, DEFAULT_DAOFIELD);
		return difference.size()==1 && difference.contains("value");
	}
	
	static List<Method> listMethods(Class<?> clazz) {
		Method[] unsortedMethods = clazz.getDeclaredMethods();
		Map<String, Method> methodMapping = new HashMap<>();
		for (Method method : unsortedMethods) {
			methodMapping.put(method.getName()+Type.getMethodDescriptor(method), method);
		}
		//Sort by line number, but if no info: give priority for methods with DAOField annotation
		List<Method> sortedMethods = new ArrayList<Method>(unsortedMethods.length);
		try(InputStream in = clazz.getResourceAsStream("/" + clazz.getName().replace('.', '/') + ".class")) {
	      if (in != null) {
	          new ClassReader(in).accept(new ClassVisitor(Opcodes.ASM7) {
	        	  @Override
		        	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
		        			String[] exceptions) {
	        		  Method methodToAdd = methodMapping.get(name+descriptor);
	        		  if(methodToAdd!=null) sortedMethods.add(methodToAdd);
	        		  return null;
		        	}
				}, ClassReader.SKIP_FRAMES);
	          return sortedMethods;
	      }
		} catch (IOException exc) {
		}
		//Ubnormal termination: so lets return original order
		return Arrays.asList(unsortedMethods);
		
	}
	
	private static void applyDAOClassAttributes(OSchemaHelper helper, DAOOClass daoOClass) {
		CustomAttribute.DOMAIN.setValue(helper.getOClass(), daoOClass.domain());
		if(!Strings.isEmpty(daoOClass.nameProperty()))
			CustomAttribute.PROP_NAME.setValue(helper.getOClass(), daoOClass.nameProperty());
		if(!Strings.isEmpty(daoOClass.parentProperty()))
			CustomAttribute.PROP_PARENT.setValue(helper.getOClass(), daoOClass.parentProperty());
		if(!Strings.isEmpty(daoOClass.defaultTab()))
			CustomAttribute.TAB.setValue(helper.getOClass(), daoOClass.defaultTab());
		if(!Strings.isEmpty(daoOClass.sortProperty())) {
			CustomAttribute.SORT_BY.setValue(helper.getOClass(), daoOClass.sortProperty());
			CustomAttribute.SORT_ORDER.setValue(helper.getOClass(), !SortOrder.DESCENDING.equals(daoOClass.sortOrder()));
		}
		if(!Strings.isEmpty(daoOClass.searchQuery()))
			CustomAttribute.SEARCH_QUERY.setValue(helper.getOClass(), daoOClass.searchQuery());
		helper.switchDisplayable(true, daoOClass.displayable());
	}
	
	private static OType getTypeByClass(Class<?> clazz) {
		OType ret = OType.getTypeByClass(clazz);
		if(OType.CUSTOM.equals(ret) && Serializable.class.isAssignableFrom(clazz)) ret = null;
		return ret;
	}
	
	private static void applyDAOFieldAttribute(OSchemaHelper helper, DAOField daoField) {
		if(daoField==null) return;
		if(!Strings.isEmpty(daoField.tab()))
			helper.assignTab(daoField.tab());
		helper.assignVisualization(daoField.visualization());
		if(!Strings.isEmpty(daoField.feature()))
			CustomAttribute.FEATURE.setValue(helper.getOProperty(), daoField.feature());
		helper.getOProperty().setMandatory(daoField.mandatory());
		helper.getOProperty().setReadonly(daoField.readOnly());
		CustomAttribute.UI_READONLY.setValue(helper.getOProperty(), daoField.uiReadOnly());
		if(!Strings.isEmpty(daoField.min())) helper.min(daoField.min());
		if(!Strings.isEmpty(daoField.max())) helper.max(daoField.max());
		if(!Strings.isEmpty(daoField.regexp())) helper.getOProperty().setRegexp(daoField.regexp());
		if(!Strings.isEmpty(daoField.collate())) helper.getOProperty().setCollate(daoField.collate());
		CustomAttribute.DISPLAYABLE.setValue(helper.getOProperty(), daoField.displayable());
		CustomAttribute.HIDDEN.setValue(helper.getOProperty(), daoField.hidden());
		if(!Strings.isEmpty(daoField.script())) {
			helper.calculateBy(daoField.script());
		} else {
			CustomAttribute.CALCULABLE.setValue(helper.getOProperty(), false);
		}
		helper.defaultValue(Strings.isEmpty(daoField.defaultValue())?null:daoField.defaultValue());
	}

}