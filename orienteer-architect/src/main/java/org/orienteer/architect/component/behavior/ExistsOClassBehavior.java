package org.orienteer.architect.component.behavior;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.core.OrienteerWebApplication;

/**
 * Behavior for checks if given {@link com.orientechnologies.orient.core.metadata.schema.OClass} exists in database
 */
public class ExistsOClassBehavior extends AbstractDefaultAjaxBehavior {

    private static final String EXISTS_CLASS_VAR = "existsClassName";

    @Override
    protected void respond(AjaxRequestTarget target) {
        IRequestParameters params = RequestCycle.get().getRequest().getRequestParameters();
        String json = params.getParameterValue(EXISTS_CLASS_VAR).toString();
        String className = getClassNameFromJson(json);
        Boolean exists = false;
        if (!Strings.isNullOrEmpty(className)) {
            OSchema schema = OrienteerWebApplication.get().getDatabase().getMetadata().getSchema();
            exists = schema.existsClass(className);
        }
        target.appendJavaScript(String.format("app.executeCallback(%s);", exists.toString()));
    }

    private String getClassNameFromJson(String json) {
        JSONObject obj = new JSONObject(json);
        return obj.getString(EXISTS_CLASS_VAR);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(OnLoadHeaderItem.forScript(
                String.format("app.setExistsOClassRequest('%s');", getCallbackUrl())));
    }
}
