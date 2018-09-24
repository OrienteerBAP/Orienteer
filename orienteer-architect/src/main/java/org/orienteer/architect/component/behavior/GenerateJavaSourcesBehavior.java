package org.orienteer.architect.component.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.architect.component.panel.JavaSourcesPanel;
import org.orienteer.architect.component.widget.OArchitectEditorWidget;
import org.orienteer.architect.event.OpenModalWindowEvent;
import org.orienteer.architect.util.JsonUtil;
import org.orienteer.architect.util.OArchitectJsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateJavaSourcesBehavior extends AbstractDefaultAjaxBehavior {

    private static final Logger LOG = LoggerFactory.getLogger(GenerateJavaSourcesBehavior.class);

    private static final String JSON_VAR = "json";

    private final OArchitectEditorWidget widget;

    public GenerateJavaSourcesBehavior(OArchitectEditorWidget widget) {
        this.widget = widget;
    }

    @Override
    protected void respond(AjaxRequestTarget target) {
        IRequestParameters params = RequestCycle.get().getRequest().getRequestParameters();
        String json = params.getParameterValue(JSON_VAR).toString("[]");
        target.prependJavaScript(OArchitectJsUtils.switchPageScroll(true));
        widget.onModalWindowEvent(
                new OpenModalWindowEvent(
                        target,
                        new ResourceModel("widget.architect.editor.java.sources"),
                        id -> new JavaSourcesPanel(id, new ListModel<>(JsonUtil.fromJSON(json)))
                )
        );
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(OnLoadHeaderItem.forScript(String.format("app.setGenerateJavaSources('%s');",
                getCallbackUrl())));
    }
}
