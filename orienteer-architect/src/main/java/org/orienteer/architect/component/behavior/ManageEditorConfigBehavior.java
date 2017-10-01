package org.orienteer.architect.component.behavior;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.architect.OArchitectModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage OArchitect editor config behavior.
 * Save config when user want.
 * Open and apply config when user open {@link org.orienteer.architect.component.widget.OArchitectEditorWidget}
 */
public class ManageEditorConfigBehavior extends AbstractDefaultAjaxBehavior {

    private static final Logger LOG = LoggerFactory.getLogger(ManageEditorConfigBehavior.class);

    private static final String CONFIG_VAR = "config";
    private IModel<ODocument> model;

    private boolean actionActive = false;

    public ManageEditorConfigBehavior(IModel<ODocument> model) {
        this.model = model;
    }

    @Override
    protected void respond(AjaxRequestTarget target) {
        if (actionActive)
            return;
        actionActive = true;
        IRequestParameters params = RequestCycle.get().getRequest().getRequestParameters();
        try {
            ODocument document = model.getObject();
            document.field(OArchitectModule.CONFIG_OPROPERTY, params.getParameterValue(CONFIG_VAR));
            document.save();
            target.appendJavaScript("app.executeCallback({save: true});");
        } catch (Exception ex) {
            LOG.error("Can't save editor config to database: {}", ex);
            target.appendJavaScript("app.executeCallback({save: false});");
        }
        actionActive = false;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        ODocument document = model.getObject();
        String xml = document.field(OArchitectModule.CONFIG_OPROPERTY);
        if (Strings.isNullOrEmpty(xml)) xml = "";
        response.render(OnLoadHeaderItem.forScript(String.format("app.setSaveEditorConfig('%s', '%s');",
                getCallbackUrl(), xml)));
    }

}
