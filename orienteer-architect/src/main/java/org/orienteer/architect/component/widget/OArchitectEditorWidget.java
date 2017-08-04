package org.orienteer.architect.component.widget;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.CallbackParameter;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.orienteer.architect.OArchitectModule;
import org.orienteer.architect.util.JsonUtil;
import org.orienteer.architect.util.OArchitectOClass;
import org.orienteer.architect.util.OArchitectOProperty;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;
import java.util.Map;

/**
 * Editor widget for OrientDB Schema
 */
@Widget(id="architect-editor", domain = "document", selector = OArchitectModule.ODATA_MODEL_OCLASS, autoEnable = true, order=10)
@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions = OrientPermission.CREATE)
public class OArchitectEditorWidget extends AbstractWidget<ODocument> {

    private static final Logger LOG = LoggerFactory.getLogger(OArchitectEditorWidget.class);

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
        add(container);
        add(createConfigBehavior());
        add(createApplyEditorChangesBehavior());
    }

    private WebMarkupContainer newContainer(String id) {
        WebMarkupContainer container = new WebMarkupContainer(id);
        container.setOutputMarkupId(true);
        return container;
    }

    private Behavior createConfigBehavior() {
        return new AbstractDefaultAjaxBehavior() {
            private final String var = "config";

            @Override
            protected void respond(AjaxRequestTarget target) {
                IRequestParameters params = RequestCycle.get().getRequest().getRequestParameters();
                LOG.debug("Save editor config: {}", params.getParameterValue(var));
                IModel<ODocument> model = OArchitectEditorWidget.this.getModel();
                ODocument document = model.getObject();
                document.field(OArchitectModule.CONFIG_OPROPERTY, params.getParameterValue(var));
                document.save();
            }

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                IModel<ODocument> model = OArchitectEditorWidget.this.getModel();
                ODocument document = model.getObject();
                String xml = document.field(OArchitectModule.CONFIG_OPROPERTY);
                if (Strings.isNullOrEmpty(xml)) xml = "";
                response.render(OnLoadHeaderItem.forScript(String.format("app.setSaveEditorConfig(%s, '%s');",
                        getCallbackFunction(CallbackParameter.explicit(var)), xml)));
            }
        };
    }

    private Behavior createApplyEditorChangesBehavior() {
        return new AbstractDefaultAjaxBehavior() {
            private final String var = "json";

            @Override
            protected void respond(AjaxRequestTarget target) {
                IRequestParameters params = RequestCycle.get().getRequest().getRequestParameters();
                String json = params.getParameterValue(var).toString("");
                LOG.debug("Apply editor changes: {}", json);
                List<OArchitectOClass> classes;
                try {
                    classes = JsonUtil.convertFromJSON(json);
                } catch (Exception ex) {
                    throw new WicketRuntimeException("Can't parse input json!", ex);
                }
                writeClassesToSchema(classes);
            }

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                response.render(OnLoadHeaderItem.forScript(String.format("app.setApplyEditorChanges(%s);",
                        getCallbackFunction(CallbackParameter.explicit(var)))));
            }
        };
    }

    private void writeClassesToSchema(final List<OArchitectOClass> classes) {
        new DBClosure<Void>() {
            @Override
            protected Void execute(ODatabaseDocument db) {
                db.commit();
                OSchema schema = db.getMetadata().getSchema();
                for (OArchitectOClass oArchitectOClass : classes) {
                    String name = oArchitectOClass.getName();
                    OClass oClass = schema.getOrCreateClass(name);
                    addSuperClassesToOClass(schema, oClass, oArchitectOClass.getSuperClasses());
                    addPropertiesToOClass(oClass, oArchitectOClass.getProperties());
                    LOG.debug("Create class: {}", oClass);
                }
                return null;
            }
        }.execute();
    }

    private void addSuperClassesToOClass(OSchema schema, OClass oClass, List<String> superClassNames) {
        if (superClassNames != null && !superClassNames.isEmpty()) {
            List<OClass> superClasses = Lists.newArrayList();
            for (String name : superClassNames) {
                OClass superClass = schema.getOrCreateClass(name);
                superClasses.add(superClass);
            }
            oClass.setSuperClasses(superClasses);
        }
    }

    private void addPropertiesToOClass(OClass oClass, List<OArchitectOProperty> properties) {
        for (OArchitectOProperty property : properties) {
            oClass.createProperty(property.getName(), property.getType());
        }
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
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(OArchitectEditorWidget.class, "js/actions.js")));
        PackageResourceReference configXml = new PackageResourceReference(OArchitectEditorWidget.class, "js/architect.js");
        String configUrl = urlFor(configXml, null).toString();
        String baseUrl = configUrl.substring(0, configUrl.indexOf("js/architect"));
        
        TextTemplate configTemplate = new PackageTextTemplate(OArchitectEditorWidget.class, "config.tmpl.xml");
        Map<String, Object> params = CommonUtils.toMap("basePath", baseUrl);
        String config = configTemplate.asString(params);
        response.render(OnLoadHeaderItem.forScript(String.format("init('%s', %s, '%s', '%s', '%s', '%s');",
											        		baseUrl,
											        		CommonUtils.escapeAndWrapAsJavaScriptString(config),
											                container.getMarkupId(),
											                editor.getMarkupId(),
											                sidebar.getMarkupId(),
											                toolbar.getMarkupId())));
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
