package org.orienteer.architect.component.behavior;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.architect.util.JsonUtil;
import org.orienteer.architect.util.OArchitectOClass;
import org.orienteer.architect.util.OArchitectOProperty;
import org.orienteer.core.OrienteerWebApplication;

import java.util.List;

/**
 * Apply OArchitect editor changes behavior.
 * When runs this behavior changes in editor write to OrientDB schema
 */
public class ApplyEditorChangesBehavior extends AbstractDefaultAjaxBehavior {

    private static final String JSON_VAR = "json";

    @Override
    protected void respond(AjaxRequestTarget target) {
        IRequestParameters params = RequestCycle.get().getRequest().getRequestParameters();
        String json = params.getParameterValue(JSON_VAR).toString("[]");
        List<OArchitectOClass> classes;
        try {
            classes = JsonUtil.fromJSON(json);
        } catch (Exception ex) {
            throw new WicketRuntimeException("Can't parse input json!", ex);
        }
        addClassesToSchema(classes);
        target.appendJavaScript("; app.checksAboutClassesChanges();");
    }

    private void addClassesToSchema(List<OArchitectOClass> classes) {
        ODatabaseDocument db = OrienteerWebApplication.get().getDatabase();
        db.commit();
        OSchema schema = db.getMetadata().getSchema();
        for (OArchitectOClass architectOClass : classes) {
            addClassToSchema(schema, architectOClass);
        }
        db.commit();
    }

    private OClass addClassToSchema(OSchema schema, OArchitectOClass architectOClass) {
        String name = architectOClass.getName();
        OClass oClass = schema.getOrCreateClass(name);
        if (architectOClass.isExistsInDb())
            removePropertiesFromOClass(oClass, architectOClass.getPropertiesForDelete());
        addSuperClassesToOClass(schema, oClass, architectOClass.getSuperClasses());
        addPropertiesToOClass(schema, oClass, architectOClass.getProperties());
        return oClass;
    }

    private void addSuperClassesToOClass(OSchema schema, OClass oClass, List<String> superClassNames) {
        if (superClassNames != null && !superClassNames.isEmpty()) {
            List<OClass> superClasses = Lists.newArrayList();
            for (String architectSuperClass : superClassNames) {
                if (schema.existsClass(architectSuperClass)) {
                    OClass superClass = schema.getClass(architectSuperClass);
                    superClasses.add(superClass);
                }
            }
            oClass.setSuperClasses(superClasses);
        }
    }

    private void removePropertiesFromOClass(OClass oClass, List<OArchitectOProperty> propertiesForDelete) {
        for (OArchitectOProperty property : propertiesForDelete) {
            OProperty oProperty = oClass.getProperty(property.getName());
            if (oProperty != null) {
                oClass.dropProperty(oProperty.getName());
            }
        }
    }

    private void addPropertiesToOClass(OSchema schema, OClass oClass, List<OArchitectOProperty> properties) {
        for (OArchitectOProperty property : properties) {
            if (!property.isSubClassProperty()) {
                OProperty oProperty = oClass.getProperty(property.getName());
                if (oProperty == null) {
                    oProperty = oClass.createProperty(property.getName(), property.getType());
                } else if (oProperty.getType() != property.getType()) {
                    oProperty.setType(property.getType());
                }
                if (!Strings.isNullOrEmpty(property.getLinkedClass())) {
                    OClass linkedClass = schema.getOrCreateClass(property.getLinkedClass());
                    oProperty.setLinkedClass(linkedClass);
                }
            }
        }
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(OnLoadHeaderItem.forScript(String.format("app.setApplyEditorChanges('%s');",
               getCallbackUrl())));
    }

}
