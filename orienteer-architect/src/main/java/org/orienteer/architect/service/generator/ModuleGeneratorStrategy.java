package org.orienteer.architect.service.generator;

import org.orienteer.architect.model.OArchitectOClass;
import org.orienteer.architect.model.OArchitectOProperty;
import org.orienteer.architect.model.generator.GeneratorMode;
import org.orienteer.architect.model.generator.OModuleSource;

import java.util.*;
import java.util.stream.Collectors;

public class ModuleGeneratorStrategy implements IGeneratorStrategy {

    public ModuleGeneratorStrategy() {
    }

    @Override
    public OModuleSource apply(List<OArchitectOClass> classes) {
        OModuleSource source = new OModuleSource();
        source.setName(GeneratorMode.MODULE.getName());
        source.setSrc(createSources(classes));
        return source;
    }

    private String createSources(List<OArchitectOClass> classes) {
        StringBuilder sb = new StringBuilder();
        appendConstants(sb, classes);

        sb.append("\n\n\n");

        appendSchemaHelperActions(sb, classes);

        sb.append("\n\n\n");

        appendSchemaSetupRelationShips(sb, classes);
        return sb.toString();
    }

    private void appendConstants(StringBuilder sb, List<OArchitectOClass> classes) {
        classes.forEach(oClass -> {
            String name = oClass.getName();

            sb.append('\n').append(staticFieldOClass(name));

            oClass.getProperties().forEach(property ->
                sb.append("\n")
                        .append(staticFieldOProperty(name, property.getName()))
            );
            sb.append('\n');
        });
    }

    private void appendSchemaHelperActions(StringBuilder sb, List<OArchitectOClass> classes) {
        sb.append(bindSchema());

        classes.forEach(oClass -> {
            String name = oClass.getName();

            sb.append('\n').append(helperOClass(name));

            oClass.getProperties().forEach(property ->
                sb.append("\n    ")
                        .append(
                                helperOProperty(name, property.getName(), property.getType().name(), property.getOrder())
                        )
            );
            sb.append(";\n");
        });
    }

    private void appendSchemaSetupRelationShips(StringBuilder sb, List<OArchitectOClass> classes) {
        Map<OArchitectOClass, List<OArchitectOProperty>> inverseClasses = getInverseEnabledClasses(classes);

        inverseClasses.forEach((oClass, properties) -> {
            String name = oClass.getName();

            properties.forEach(property ->
                sb.append(
                        helperSetupRelationship(
                                name,
                                property.getName(),
                                property.getLinkedClass(),
                                property.getInverseProperty().getName()
                        )
                )
            );
        });
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

    private String staticFieldOClass(String name) {
        return String.format("public static final String %s = \"%s\";", constantOClass(name), name);
    }

    private String staticFieldOProperty(String className, String property) {
        return String.format("public static final String %s = \"%s\";",
                constantOProperty(className, property), property);
    }

    private String helperOClass(String name) {
        return String.format("%s.oClass(%s)", helper(), constantOClass(name));
    }

    private String helperOProperty(String className, String property, String type, int order) {
        return String.format(".oProperty(%s, OType.%s, %s)", constantOProperty(className, property), type, order);
    }

    private String helperSetupRelationship(String class1, String prop1, String class2, String prop2) {
        return String.format(
                "%s.setupRelationship(%s, %s, %s, %s);",
                helper(),
                constantOClass(class1),
                constantOProperty(class1, prop1),
                constantOClass(class2),
                constantOProperty(class2, prop2)
        );
    }

    private String constantOClass(String name) {
        return String.format("%s_CLASS_NAME", name.toUpperCase());
    }

    private String constantOProperty(String className, String propertyName) {
        return String.format("%s_PROP_%s", className.toUpperCase(), propertyName.toUpperCase());
    }

    private String bindSchema() {
        return String.format("OSchemaHelper %s = OSchemaHelper.bind(db);", helper());
    }

    private String helper() {
        return "helper";
    }
}
