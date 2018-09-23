package org.orienteer.architect.service.generator;

import com.google.common.base.Strings;
import org.orienteer.architect.model.OArchitectOClass;
import org.orienteer.architect.model.OArchitectOProperty;
import org.orienteer.architect.model.generator.GeneratorMode;
import org.orienteer.architect.model.generator.OModuleSource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.orienteer.architect.util.OSourceUtil.wrapString;

public class ModuleGeneratorStrategy implements IGeneratorStrategy {

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
        result.addSource(new OSourceBlankLine());
        result.addSources(createConstants(classes));
        result.addSource(new OSourceBlankLine(2));
        result.addSources(createSchemaHelperBindings(classes));
        result.addSource(new OSourceBlankLine());
        result.addSources(createRelationships(classes));
        return result;
    }

    private List<ISource> createConstants(List<OArchitectOClass> classes) {
        List<ISource> constants = new LinkedList<>();
        for (OArchitectOClass oClass : classes) {
            constants.add(createOClassConstant(oClass));
            constants.addAll(createPropertiesConstants(oClass));
            constants.add(new OSourceBlankLine(2));
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

    private ISource createOClassConstant(OArchitectOClass oClass) {
        return new OSourceConstant(
                "public static final",
                "String",
                constantOClass(oClass.getName()),
                new OSourceNewInstance(null, wrapString(oClass.getName()))
        );
    }

    private List<ISource> createPropertiesConstants(OArchitectOClass oClass) {
        return oClass.getProperties().stream()
                .flatMap(property ->
                        Stream.of(
                                new OSourceBlankLine(),
                                new OSourceConstant(
                                        "public static final",
                                        "String",
                                        constantOProperty(oClass.getName(), property.getName()),
                                        new OSourceNewInstance(null, wrapString(property.getName()))
                                )
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
            sources.addAll(createOClassProperties(classes, oClass));
            sources.add(new OSourceSymbol(";\n\n"));
        }
        return sources;
    }

    private List<ISource> createRelationships(List<OArchitectOClass> classes) {
        List<ISource> sources = new LinkedList<>();
        Map<OArchitectOClass, List<OArchitectOProperty>> inverseClasses = getInverseEnabledClasses(classes);
        inverseClasses.forEach((oClass, properties) -> sources.addAll(createRelationships(classes, oClass, properties)));
        return sources;
    }

    private ISource createOClass(OArchitectOClass oClass) {
        LinkedList<String> args = new LinkedList<>(oClass.getSuperClasses());
        args.addFirst(constantOClass(oClass.getName()));
        return new OSourceCall(helper(), "oClass", args.toArray(new String[0]));
    }

    private List<ISource> createOClassProperties(List<OArchitectOClass> classes, OArchitectOClass oClass) {
        return oClass.getProperties().stream()
                .flatMap(prop ->
                        Stream.of(
                                new OSourceBlankLine(),
                                new OSourceSpace(4),
                                createProperty(classes, oClass, prop)
                        )
                ).collect(Collectors.toCollection(LinkedList::new));
    }

    private List<ISource> createRelationships(List<OArchitectOClass> classes, OArchitectOClass oClass, List<OArchitectOProperty> properties) {
        return properties.stream()
                .flatMap(prop -> Stream.of(
                        new OSourceBlankLine(),
                        createInverseBinding(classes, oClass, prop),
                        new OSourceSymbol(";")
                )).collect(Collectors.toCollection(LinkedList::new));
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

    private ISource createProperty(List<OArchitectOClass> classes, OArchitectOClass oClass, OArchitectOProperty property) {
        OSourceFragment fragment = new OSourceFragment();
        String propName = constantOProperty(oClass.getName(), property.getName());
        String type = "OType." + property.getType().name();
        String order = "" + property.getOrder();
        String linkedClass = property.getLinkedClass();

        fragment.addSource(new OSourceChainCall("oProperty", propName, type, order));
        if (!property.isInversePropertyEnable() && !Strings.isNullOrEmpty(linkedClass)) {
            if (isClassContainsIn(linkedClass, classes)) {
                linkedClass = constantOClass(linkedClass);
            } else {
                linkedClass = wrapString(linkedClass);
            }
            fragment.addSource(new OSourceChainCall("linkedClass", linkedClass));
        }
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

    private Map<OArchitectOClass, List<OArchitectOProperty>> getInverseEnabledClasses(List<OArchitectOClass> classes) {
        Map<OArchitectOClass, List<OArchitectOProperty>> inverseClasses = new LinkedHashMap<>();
        Set<String> alreadyLinked = new HashSet<>();

        classes.stream()
                .filter(oClass -> !alreadyLinked.contains(oClass.getName()))
                .forEach(oClass -> {
                    List<OArchitectOProperty> properties = getInverseProperties(oClass.getProperties());
                    if (!properties.isEmpty()) {
                        inverseClasses.put(oClass, properties);
                        alreadyLinked.addAll(getClassNamesFromInverseProperties(properties));
                    }
                });
        return inverseClasses;
    }

    private List<OArchitectOProperty> getInverseProperties(List<OArchitectOProperty> properties) {
        return properties.stream()
                .filter(OArchitectOProperty::isInversePropertyEnable)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private Set<String> getClassNamesFromInverseProperties(List<OArchitectOProperty> properties) {
        return properties.stream()
                .map(OArchitectOProperty::getLinkedClass)
                .collect(Collectors.toSet());
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
}
