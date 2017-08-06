package org.orienteer.architect.component.widget;

import com.orientechnologies.orient.core.record.impl.ODocument;
import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.orienteer.architect.OArchitectModule;
import org.orienteer.architect.component.behavior.ApplyEditorChangesBehavior;
import org.orienteer.architect.component.behavior.GetOClassesBehavior;
import org.orienteer.architect.component.behavior.ManageEditorConfigBehavior;
import org.orienteer.architect.component.panel.SchemaOClassesPanel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import java.util.Map;

/**
 * Editor widget for OrientDB Schema
 */
@Widget(id="architect-editor", domain = "document", selector = OArchitectModule.ODATA_MODEL_OCLASS, autoEnable = true, order=10)
@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions = {
        OrientPermission.CREATE, OrientPermission.UPDATE, OrientPermission.DELETE, OrientPermission.READ
})
public class OArchitectEditorWidget extends AbstractWidget<ODocument> {

    private static final JavaScriptResourceReference MXGRAPH_JS = new WebjarsJavaScriptResourceReference("mxgraph/current/javascript/mxClient.min.js");
    private static final CssResourceReference MXGRAPH_CSS    = new WebjarsCssResourceReference("mxgraph/current/javascript/src/css/common.css");
    private static final CssResourceReference OARCHITECT_CSS = new CssResourceReference(OArchitectEditorWidget.class, "css/architect.css");

    private WebMarkupContainer container;
    private WebMarkupContainer editor;
    private WebMarkupContainer toolbar;
    private WebMarkupContainer sidebar;


    public OArchitectEditorWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        container = newContainer("container");
        container.add(editor = newContainer("editor"));
        container.add(toolbar = newContainer("toolbar"));
        container.add(sidebar = newContainer("sidebar"));
        SchemaOClassesPanel panel = new SchemaOClassesPanel("listClasses", "; app.executeCallback('%s');");
        container.add(panel);
        add(container);
        add(new ManageEditorConfigBehavior(getModel()));
        add(new ApplyEditorChangesBehavior());
        add(new GetOClassesBehavior(panel));
    }

    private WebMarkupContainer newContainer(String id) {
        WebMarkupContainer container = new WebMarkupContainer(id);
        container.setOutputMarkupId(true);
        return container;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssReferenceHeaderItem.forReference(OARCHITECT_CSS));
        response.render(CssReferenceHeaderItem.forReference(MXGRAPH_CSS));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(OArchitectEditorWidget.class, "js/architect.js")));
        response.render(JavaScriptHeaderItem.forScript(
                String.format("; initMxGraph('%s');", "en"), null));
        response.render(JavaScriptHeaderItem.forReference(MXGRAPH_JS));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(OArchitectEditorWidget.class, "js/editor.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(OArchitectEditorWidget.class, "js/editor-bar.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(OArchitectEditorWidget.class, "js/metadata.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(OArchitectEditorWidget.class, "js/editor-modal-window.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(OArchitectEditorWidget.class, "js/editor-popup-menu.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(OArchitectEditorWidget.class, "js/editor-value-container.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(OArchitectEditorWidget.class, "js/actions.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(OArchitectEditorWidget.class, "js/constants.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(OArchitectEditorWidget.class, "js/util.js")));
        String locale = getOArchitectEditorLocale();
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(OArchitectEditorWidget.class,
                String.format("js/locale/architect_%s.js", locale))));

        PackageResourceReference configXml = new PackageResourceReference(OArchitectEditorWidget.class, "js/architect.js");
        String configUrl = urlFor(configXml, null).toString();
        String baseUrl = configUrl.substring(0, configUrl.indexOf("js/architect"));
        
        TextTemplate configTemplate = new PackageTextTemplate(OArchitectEditorWidget.class, "config.tmpl.xml");
        Map<String, Object> params = CommonUtils.toMap("basePath", baseUrl);
        String config = configTemplate.asString(params);
        response.render(OnLoadHeaderItem.forScript(String.format("init('%s', %s, %s, '%s', '%s', '%s', '%s');",
											        		baseUrl,
											        		CommonUtils.escapeAndWrapAsJavaScriptString(config),
											                locale,
											                container.getMarkupId(),
											                editor.getMarkupId(),
											                sidebar.getMarkupId(),
											                toolbar.getMarkupId())));
    }

    private String getOArchitectEditorLocale() {
        String locale = getLocale().getLanguage();
        if (locale.equals("en") || locale.equals("ru") || locale.equals("uk"))
            return locale;
        return "en";
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.edit);
    }

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return new ResourceModel("widget.architect.editor.title");
    }
    
    @Override
    protected String getWidgetStyleClass() {
    	return "strict";
    }

}
