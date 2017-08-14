package org.orienteer.architect.component.behavior;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.core.OrienteerWebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behavior for sending Orienteer {@link com.orientechnologies.orient.core.metadata.schema.OClass} page URL 
 * and Orienteer {@link com.orientechnologies.orient.core.metadata.schema.OProperty} page URL
 */
public class ShowMoreInformationBehavior extends AbstractDefaultAjaxBehavior {

    private static final Logger LOG = LoggerFactory.getLogger(ShowMoreInformationBehavior.class);

    private static final String CLASS_VAR   = "class";
    private static final String PROPERTY_VAR = "property";
    
    @Override
    protected void respond(AjaxRequestTarget target) {
        IRequestParameters params = RequestCycle.get().getRequest().getRequestParameters();
        String className = params.getParameterValue(CLASS_VAR).toString();
        String propertyName = params.getParameterValue(PROPERTY_VAR).toString();
        String response = "";
        if (!Strings.isNullOrEmpty(className)) {
            if (!Strings.isNullOrEmpty(propertyName)) {
                response = getPropertyPageUrl(className, propertyName);
            } else response = getClassPageUrl(className);
        }

        target.appendJavaScript(String.format("; app.executeCallback('%s')", response));
    }

    private String getClassPageUrl(String className) {
        String url = "";
        OSchema schema = OrienteerWebApplication.get().getDatabase().getMetadata().getSchema();
        if (schema.existsClass(className)) {
            url = "/class/" + className;
        }
        LOG.info("URL: {}", url);
        return url;
    }

    private String getPropertyPageUrl(String className, String propertyName) {
        String url = "";
        OSchema schema = OrienteerWebApplication.get().getDatabase().getMetadata().getSchema();
        if (schema.existsClass(className)) {
            OProperty property = schema.getClass(className).getProperty(propertyName);
            if (property != null) {
                url = "/property/" + className + "/" + property.getName();
            }
        }
        return url;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(OnLoadHeaderItem.forScript(String.format("; app.setShowMoreInfoRequest('%s');", getCallbackUrl())));
    }
}
