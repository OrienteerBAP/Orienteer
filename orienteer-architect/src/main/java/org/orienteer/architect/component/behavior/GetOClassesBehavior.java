package org.orienteer.architect.component.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.CallbackParameter;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.architect.component.panel.IOArchitectOClassesManager;
import org.orienteer.architect.util.JsonUtil;

/**
 * Get collection of {@link com.orientechnologies.orient.core.metadata.schema.OClass} from OrientDB behavior.
 */
public class GetOClassesBehavior extends AbstractDefaultAjaxBehavior {

    private static final String EXISTS_CLASSES_VAR = "existsClasses";

    private final IOArchitectOClassesManager manager;

    public GetOClassesBehavior(IOArchitectOClassesManager manager) {
        this.manager = manager;
    }

    @Override
    protected void respond(AjaxRequestTarget target) {
        IRequestParameters params = RequestCycle.get().getRequest().getRequestParameters();
        String json = params.getParameterValue(EXISTS_CLASSES_VAR).toString("[]");
        manager.setExistsClasses(JsonUtil.fromJSON(json));
        manager.switchModalWindow(target, true);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(OnLoadHeaderItem.forScript(String.format("; app.setGetOClassesRequest(%s);",
                getCallbackFunction(CallbackParameter.explicit(EXISTS_CLASSES_VAR)))));
    }
}
