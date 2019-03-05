package org.orienteer.architect.service.generator;

import org.orienteer.architect.model.OArchitectOClass;
import org.orienteer.architect.model.OArchitectOProperty;
import org.orienteer.architect.model.generator.GeneratorMode;
import org.orienteer.architect.model.generator.OModuleSource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.orienteer.architect.util.OSourceUtil.wrapString;

/**
 * Implementation of {@link IGeneratorStrategy} which generates source code for Orienteer module.
 * Example:
 * {@code
 * public static final String EMPLOYEE_CLASS_NAME = "Employee";
 * public static final String EMPLOYEE_PROP_ID = "id";
 * public static final String EMPLOYEE_PROP_NAME = "name";
 * public static final String EMPLOYEE_PROP_WORKPLACE = "workPlace";
 *
 * public static final String WORKPLACE_CLASS_NAME = "WorkPlace";
 * public static final String WORKPLACE_PROP_ID = "id";
 * public static final String WORKPLACE_PROP_NAME = "name";
 * public static final String WORKPLACE_PROP_EMPLOYEES = "employees";
 *
 *
 *
 * OSchemaHelper helper = OSchemaHelper.bind(db);
 * helper.oClass(EMPLOYEE_CLASS_NAME)
 *     .oProperty(EMPLOYEE_PROP_ID, OType.INTEGER, 0)
 *     .oProperty(EMPLOYEE_PROP_NAME, OType.STRING, 10)
 *     .oProperty(EMPLOYEE_PROP_WORKPLACE, OType.LINK, 20);
 *
 * helper.oClass(WORKPLACE_CLASS_NAME)
 *    .oProperty(WORKPLACE_PROP_ID, OType.INTEGER, 0)
 *    .oProperty(WORKPLACE_PROP_NAME, OType.STRING, 10)
 *    .oProperty(WORKPLACE_PROP_EMPLOYEES, OType.LINKLIST, 20);
 *
 *
 *
 * helper.setupRelationship(EMPLOYEE_CLASS_NAME, EMPLOYEE_PROP_WORKPLACE, WORKPLACE_CLASS_NAME, WORKPLACE_PROP_EMPLOYEES);
 * }
 */
public class ModuleGeneratorStrategy implements IGeneratorStrategy {

    private List<OSourceConstant> constants;

    public ModuleGeneratorStrategy() {

    }

    @Override
    public OModuleSource apply(List<OArchitectOClass> classes) {
        OModuleSource source = new OModuleSource();
        source.setName(GeneratorMode.MODULE.getName());
        source.setSrc(toSourceFragment(classes).toJavaSrc());
        return source;
    }

    private ISource toSourceFragment(List<OArchitectOClass> classes) {
        OSourceFragment result = new OSourceFragment();

        constants = createConstants(classes);

        result.addSource(new OSourceBlankLine());
        result.addSources(constants);
        result.addSource(new OSourceBlankLine(3));
        result.addSources(createSchemaHelperBindings(classes));
        result.addSource(new OSourceBlankLine());
        result.addSources(createRelationships(classes));
        return result;
    }

    private List<OSourceConstant> createConstants(List<OArchitectOClass> classes) {
        List<OSourceConstant> constants = new LinkedList<>();
        for (OArchitectOClass oClass : classes) {
            constants.add(createOClassConstant(oClass));
            constants.addAll(createPropertiesConstants(oClass));
        }
        return constants;
    }

    private List<ISource> createSchemaHelperBindings(List<OArchitectOClass> classes) {
        List<ISource> bindings = new LinkedList<>();
        bindings.add(createBindSchemaHelper());
        bindings.add(new OSourceBlankLine());
        bindings.addAll(createOClassBindings(classes));
        return bindings;
    }

    private OSourceConstant createOClassConstant(OArchitectOClass oClass) {
        return new OSourceConstant(
                "public static final",
                "String",
                constantOClass(oClass.getName()),
                new OSourceNewInstance(null, wrapString(oClass.getName()))
        );
    }

    private List<OSourceConstant> createPropertiesConstants(OArchitectOClass oClass) {
        return oClass.getProperties().stream()
                .filter(p -> !p.isSubClassProperty())
                .map(property ->
                        new OSourceConstant(
                                "public static final",
                                "String",
                                constantOProperty(oClass.getName(), property.getName()),
                                new OSourceNewInstance(null, wrapString(property.getName()))
                        )
                ).collect(Collectors.toCollection(LinkedList::new));
    }

    private ISource createBindSchemaHelper() {
        return new OSourceVariable(
                "OSchemaHelper",
                helper(),
                new OSourceStaticNewInstance("OSchemaHelper", "bind", "db")
        );
    }

    private List<ISource> createOClassBindings(List<OArchitectOClass> classes) {
        List<ISource> sources = new LinkedList<>();
        for (OArchitectOClass oClass : classes) {
            sources.add(createOClass(oClass));
            sources.addAll(createOClassProperties(oClass));
            sources.add(new OSourceSymbol(";\n\n"));
        }
        return sources;
    }

    private List<ISource> createRelationships(List<OArchitectOClass> classes) {
        List<ISource> sources = new LinkedList<>();

        Map<String, Set<String>> inverseProperties = new HashMap<>();

        for (OArchitectOClass oClass : classes) {
            List<OArchitectOProperty> properties = oClass.getProperties();
            for (OArchitectOProperty property : properties) {
                boolean isInverse = property.isInversePropertyEnable();
                boolean isLink = property.getLinkedClass() != null;

                if (isInverse || isLink) {
                    sources.add(new OSourceBlankLine());
                }

                if (isInverse) {
                    OArchitectOProperty inverseProperty = property.getInverseProperty();
                    boolean propContains = isContainsProperty(oClass.getName(), property.getName(), inverseProperties);
                    boolean inversePropContains = isContainsProperty(property.getLinkedClass(), inverseProperty.getName(), inverseProperties);

                    if (!propContains && !inversePropContains) {
                        sources.add(createInverseBinding(classes, oClass, property));

                        cacheProperties(property, inverseProperty, inverseProperties);
                    }
                } else if (isLink) {
                    sources.add(createLinkBinding(classes, oClass, property));
                }

                if (isInverse || isLink) {
                    sources.add(new OSourceSymbol(";"));
                }
            }
        }

        return sources;
    }

    private ISource createOClass(OArchitectOClass oClass) {
        LinkedList<String> args = new LinkedList<>(constantSuperClasses(oClass.getSuperClasses()));
        args.addFirst(constantOClass(oClass.getName()));
        return new OSourceCall(helper(), "oClass", args.toArray(new String[0]));
    }

    private List<ISource> createOClassProperties(OArchitectOClass oClass) {
        return oClass.getProperties().stream()
                .filter(p -> !p.isSubClassProperty())
                .flatMap(prop ->
                        Stream.of(
                                new OSourceBlankLine(),
                                new OSourceSpace(4),
                                createProperty(oClass, prop)
                        )
                ).collect(Collectors.toCollection(LinkedList::new));
    }

    private ISource createInverseBinding(List<OArchitectOClass> classes, OArchitectOClass oClass, OArchitectOProperty property) {
        String class1 = constantOClass(oClass.getName());
        String prop1 = constantOProperty(oClass.getName(), property.getName());
        String class2 = property.getLinkedClass();
        String prop2 = property.getInverseProperty().getName();
        if (isClassContainsIn(class2, classes)) {
            prop2 = constantOProperty(class2, prop2);
            class2 = constantOClass(class2);
        } else {
            prop2 = wrapString(prop2);
            class2 = wrapString(class2);
        }
        return new OSourceCall(helper(), "setupRelationship", class1, prop1, class2, prop2);
    }

    private ISource createLinkBinding(List<OArchitectOClass> classes, OArchitectOClass oClass, OArchitectOProperty property) {
        String className = "\"" + oClass.getName() + "\"";

        className = getConstantByValue(className).map(OSourceConstant::getName).orElse(className);

        String propName = constantOProperty(oClass.getName(), property.getName());
        String linkedClass = property.getLinkedClass();

        if (isClassContainsIn(linkedClass, classes)) {
            linkedClass = constantOClass(linkedClass);
        } else {
            linkedClass = wrapString(linkedClass);
        }
        return new OSourceCall("helper", "setupRelationship", className, propName, linkedClass);
    }

    private ISource createProperty(OArchitectOClass oClass, OArchitectOProperty property) {
        OSourceFragment fragment = new OSourceFragment();
        String propName = constantOProperty(oClass.getName(), property.getName());
        String type = "OType." + property.getType().name();
        String order = "" + property.getOrder();
        fragment.addSource(new OSourceChainCall("oProperty", propName, type, order));
        return fragment;
    }

    private boolean isClassContainsIn(String name, List<OArchitectOClass> classes) {
        for (OArchitectOClass oClass : classes) {
            if (oClass.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private List<String> constantSuperClasses(List<String> superClasses) {
        return superClasses.stream()
                .map(cls -> "\"" + cls + "\"")
                .map(cls -> getConstantByValue(cls)
                        .map(OSourceVariableDeclaration::getName)
                        .orElse(cls)
                ).collect(Collectors.toCollection(LinkedList::new));
    }

    private String constantOClass(String name) {
        return String.format("%s_CLASS_NAME", name.toUpperCase());
    }

    private String constantOProperty(String className, String propertyName) {
        return String.format("%s_PROP_%s", className.toUpperCase(), propertyName.toUpperCase());
    }

    protected String helper() {
        return "helper";
    }

    private Optional<OSourceConstant> getConstantByValue(String value) {
        return constants.stream()
                .filter(c -> {
                    OSourceNewInstance instance = c.getInstance();
                    List<String> args = instance.getArgs();
                    return !args.isEmpty() && value.equals(args.get(0));
                }).findFirst();
    }

    private boolean isContainsProperty(String className, String property, Map<String, Set<String>> properties) {
        if (!properties.containsKey(className)) {
            return false;
        }
        return properties.get(className).stream()
                .anyMatch(p -> Objects.equals(p, property));
    }

    private void cacheProperties(OArchitectOProperty property, OArchitectOProperty inverseProperty, Map<String, Set<String>> cache) {
        Set<String> p = cache.get(inverseProperty.getLinkedClass());
        if (p == null) {
            p = new HashSet<>();
        }
        p.add(property.getName());
        cache.put(inverseProperty.getLinkedClass(), p);

        p = cache.get(property.getLinkedClass());
        if (p == null) {
            p = new HashSet<>();
        }
        p.add(inverseProperty.getName());
        cache.put(property.getLinkedClass(), p);
    }
}
