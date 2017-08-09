package org.orienteer.architect.component.panel.command;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.architect.component.panel.IOArchitectOClassesManager;
import org.orienteer.architect.util.JsonUtil;
import org.orienteer.architect.util.OArchitectOClass;
import org.orienteer.architect.util.OArchitectOProperty;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AbstractCheckBoxEnabledCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Add collection with {@link com.orientechnologies.orient.core.metadata.schema.OClass} to OArchitect JavaScript response
 */
public class AddOClassesCommand extends AbstractCheckBoxEnabledCommand<OClass> {

    private final IOArchitectOClassesManager manager;

    public AddOClassesCommand(IModel<String> labelModel, IOArchitectOClassesManager manager) {
        super(labelModel, manager.getTable());
        this.manager = manager;
    }

    @Override
    protected void onInstantiation() {
        super.onInstantiation();
        setBootstrapType(BootstrapType.PRIMARY);
        setIcon(FAIconType.plus);
    }

    @Override
    protected void performMultiAction(AjaxRequestTarget target, List<OClass> classes) {
        List<OArchitectOClass> architectOClasses = toOArchitectOClasses(classes);
        String json = JsonUtil.toJSON(architectOClasses);
        if (Strings.isNullOrEmpty(json)) json = "[]";
        manager.executeCallback(target, json);
        manager.switchModalWindow(target, false);
    }

    private List<OArchitectOClass> toOArchitectOClasses(List<OClass> classes) {
        List<OArchitectOClass> architectOClasses = new ArrayList<>(classes.size());
        for (OClass oClass : classes) {
            architectOClasses.add(toOArchitectOClass(oClass));
        }
        return architectOClasses;
    }

    private OArchitectOClass toOArchitectOClass(OClass oClass) {
        OArchitectOClass architectOClass = new OArchitectOClass(oClass.getName());

        architectOClass.setProperties(toOArchitectProperties(oClass.properties(), oClass.getSuperClasses()));
        architectOClass.setSuperClassesNames(toOArchitectSuperClasses(oClass.getSuperClasses()));
        return architectOClass;
    }

    private List<OArchitectOProperty> toOArchitectProperties(Collection<OProperty> properties, List<OClass> superClasses) {
        List<OArchitectOProperty> architectProperties = new ArrayList<>(properties.size());
        for (OProperty property : properties) {
            OArchitectOProperty architectOProperty = new OArchitectOProperty(property.getName(), property.getType());
            if (property.getLinkedClass() != null) {
                architectOProperty.setLinkedClassName(property.getLinkedClass().getName());
            }
            architectOProperty.setSubClassProperty(isSubClassProperty(property, superClasses));
            architectProperties.add(architectOProperty);
        }
        return architectProperties;
    }

    private boolean isSubClassProperty(OProperty property, List<OClass> superClasses) {
        boolean isSubClass = false;
        for (OClass oClass : superClasses) {
            isSubClass = oClass.getProperty(property.getName()) != null;
            if (!isSubClass) {
                List<OClass> classes = oClass.getSuperClasses();
                if (classes != null && !classes.isEmpty()) {
                    isSubClass = isSubClassProperty(property, classes);
                }
            } else break;
        }
        return isSubClass;
    }

    private List<String> toOArchitectSuperClasses(List<OClass> superClasses) {
        List<String> architectSuperClasses = new ArrayList<>(superClasses.size());
        for (OClass oClass : superClasses) {
            architectSuperClasses.add(oClass.getName());
        }
        return architectSuperClasses;
    }

}
