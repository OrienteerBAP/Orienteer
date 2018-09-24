package org.orienteer.architect.component.panel;

import com.google.inject.Inject;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.architect.model.OArchitectOClass;
import org.orienteer.architect.model.generator.GeneratorMode;
import org.orienteer.architect.model.generator.OModuleSource;
import org.orienteer.architect.model.generator.OSourceGeneratorConfig;
import org.orienteer.architect.service.ISourceGenerator;

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
        add(createJavaPanel("javaPanel"));
        setOutputMarkupPlaceholderTag(true);
    }

    private Component createJavaPanel(String id) {
        return new Label(id, Model.of("")) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                if (isVisible() && getModelObject() != null) {
                    OSourceGeneratorConfig config = new OSourceGeneratorConfig();
                    config.setClasses(getModelObject());
                    config.setMode(GeneratorMode.MODULE);
                    Optional<OModuleSource> sources = sourceGenerator.generateSource(config);
                    String src = sources.map(OModuleSource::getSrc).orElseThrow(IllegalStateException::new);
                    setDefaultModelObject(src);
                } else setDefaultModelObject(null);
            }

            @Override
            protected void onInitialize() {
                super.onInitialize();
                setOutputMarkupPlaceholderTag(true);
                setEscapeModelStrings(false);
            }

        };
    }
}
