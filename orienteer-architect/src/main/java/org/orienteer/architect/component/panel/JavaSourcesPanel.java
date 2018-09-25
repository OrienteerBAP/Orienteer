package org.orienteer.architect.component.panel;

import com.google.inject.Inject;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.orienteer.architect.component.JavaCodeEditorPanel;
import org.orienteer.architect.model.OArchitectOClass;
import org.orienteer.architect.model.generator.GeneratorMode;
import org.orienteer.architect.model.generator.OModuleSource;
import org.orienteer.architect.model.generator.OSourceGeneratorConfig;
import org.orienteer.architect.service.ISourceGenerator;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.web.BasePage;

import java.util.List;
import java.util.Optional;

public class JavaSourcesPanel extends GenericPanel<List<OArchitectOClass>> {

    public JavaScriptResourceReference COPY_JS = new JavaScriptResourceReference(JavaSourcesPanel.class, "copy.js");

    @Inject
    private ISourceGenerator sourceGenerator;

    private JavaCodeEditorPanel editorPanel;

    public JavaSourcesPanel(String id, IModel<List<OArchitectOClass>> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        OSourceGeneratorConfig config = new OSourceGeneratorConfig();
        config.setClasses(getModelObject());
        config.setMode(GeneratorMode.MODULE);
        Optional<OModuleSource> sources = sourceGenerator.generateSource(config);
        String src = sources.map(OModuleSource::getSrc).orElseThrow(IllegalStateException::new);

        add(editorPanel = new JavaCodeEditorPanel("javaPanel", Model.of(src), DisplayMode.VIEW.asModel()));
        add(createCopyLink("copyLink"));
        setOutputMarkupPlaceholderTag(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(BasePage.BOOTSTRAP_CSS));
        response.render(JavaScriptHeaderItem.forReference(COPY_JS));

        response.render(OnLoadHeaderItem.forScript(initCopyJs()));
    }

    private WebMarkupContainer createCopyLink(String id) {
        return new WebMarkupContainer(id) {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                setOutputMarkupPlaceholderTag(true);
            }
        };
    }

    private String initCopyJs() {
        return String.format("addAutoCopyOnElement('%s', '%s')",
                editorPanel.getEditorArea().getMarkupId(), get("copyLink").getMarkupId());
    }
}
