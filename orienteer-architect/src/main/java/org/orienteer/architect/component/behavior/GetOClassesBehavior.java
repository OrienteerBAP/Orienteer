package org.orienteer.architect.component.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.architect.component.panel.SchemaOClassesModalPanel;
import org.orienteer.architect.component.widget.OArchitectEditorWidget;
import org.orienteer.architect.event.OpenModalWindowEvent;
import org.orienteer.architect.model.OArchitectOClass;
import org.orienteer.architect.util.JsonUtil;
import org.orienteer.architect.util.OArchitectClassesUtils;
import org.orienteer.architect.util.OArchitectJsUtils;

import java.util.List;

/**
 * Get collection of {@link com.orientechnologies.orient.core.metadata.schema.OClass} from OrientDB behavior.
 */
public class GetOClassesBehavior extends AbstractDefaultAjaxBehavior {

    private static final String EXISTS_CLASSES_VAR = "existsClasses";
    private static final String CLASSES_LIST_VAR   = "classesList";

    private final OArchitectEditorWidget widget;

    public GetOClassesBehavior(OArchitectEditorWidget widget) {
        this.widget = widget;
    }

    @Override
    protected void respond(AjaxRequestTarget target) {
        IRequestParameters params = RequestCycle.get().getRequest().getRequestParameters();
        String json = params.getParameterValue(EXISTS_CLASSES_VAR).toString("[]");
        boolean classesList = params.getParameterValue(CLASSES_LIST_VAR).toBoolean(false);
        if (classesList) {
            target.appendJavaScript(String.format("app.executeCallback('%s');", getAllClassesAsJson()));
        } else {
            target.prependJavaScript(OArchitectJsUtils.switchPageScroll(true));
            widget.onModalWindowEvent(
                    new OpenModalWindowEvent(
                            target,
                            createModalWindowTitle(),
                            id -> createPanel(id, new ListModel<>(JsonUtil.fromJSON(json)))
                    )
            );
        }
    }

    private IModel<String> createModalWindowTitle() {
        return new ResourceModel("widget.architect.editor.list.classes.title");
    }

    private Component createPanel(String id, IModel<List<OArchitectOClass>> classes) {
        return new SchemaOClassesModalPanel(id, classes);
    }

    private String getAllClassesAsJson() {
        List<OArchitectOClass> allClasses = OArchitectClassesUtils.getAllClasses();
        return JsonUtil.toJSON(allClasses);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(OnLoadHeaderItem.forScript(String.format("app.setGetOClassesRequest('%s');",
                getCallbackUrl())));
    }
}
