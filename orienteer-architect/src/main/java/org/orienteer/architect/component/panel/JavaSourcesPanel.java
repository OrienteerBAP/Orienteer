package org.orienteer.architect.component.panel;

import com.google.inject.Inject;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
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

    @Inject
    private ISourceGenerator sourceGenerator;


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

        add(new JavaCodeEditorPanel("javaPanel", Model.of(src), DisplayMode.VIEW.asModel()));

        setOutputMarkupPlaceholderTag(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(BasePage.BOOTSTRAP_CSS));
    }
}
