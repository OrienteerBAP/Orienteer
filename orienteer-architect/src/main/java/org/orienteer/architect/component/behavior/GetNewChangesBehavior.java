package org.orienteer.architect.component.behavior;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.architect.util.JsonUtil;
import org.orienteer.architect.util.OArchitectOClass;
import org.orienteer.core.OrienteerWebApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Behavior for checks if in given classes are new changes
 */
public class GetNewChangesBehavior extends AbstractDefaultAjaxBehavior {

    private static final String CLASSES_NAMES_VAR = "classesNames";

    @Override
    protected void respond(AjaxRequestTarget target) {
        IRequestParameters params = RequestCycle.get().getRequest().getRequestParameters();
        String classesNamesJson = params.getParameterValue(CLASSES_NAMES_VAR).toString();
        String json = "";
        if (!Strings.isNullOrEmpty(classesNamesJson)) {
            List<OClass> classes = getClasses(toList(classesNamesJson));
            List<OArchitectOClass> architectClasses = toArchitectOClasses(classes);
            json = JsonUtil.toJSON(architectClasses);
        }
        target.appendJavaScript(String.format("; app.executeCallback('%s'); ", json));
    }

    private List<OArchitectOClass> toArchitectOClasses(List<OClass> classes) {
        List<OArchitectOClass> architectClasses = new ArrayList<>(classes.size());
        for (OClass oClass : classes) {
            architectClasses.add(OArchitectOClass.toArchitectOClass(oClass));
        }
        return architectClasses;
    }

    private List<OClass> getClasses(List<String> classNames) {
        List<OClass> classes = new ArrayList<>(classNames.size());
        OSchema schema = OrienteerWebApplication.get().getDatabase().getMetadata().getSchema();
        for (String name : classNames) {
            if (schema.existsClass(name))
                classes.add(schema.getClass(name));
        }
        return classes;
    }

    private List<String> toList(String classesNames) {
        JSONArray jsonArray = new JSONArray(classesNames);
        List<String> result = new ArrayList<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            result.add(jsonArray.getString(i));
        }
        return result;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(OnLoadHeaderItem.forScript(
                String.format("; app.setChecksAboutClassesChanges('%s');", getCallbackUrl())));
    }
}
