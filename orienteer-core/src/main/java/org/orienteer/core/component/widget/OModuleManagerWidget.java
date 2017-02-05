package org.orienteer.core.component.widget;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.service.OrienteerFilter;
import org.orienteer.core.service.Reload;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vitaliy Gonchar
 */
@Widget(domain="schema", tab="classes", id="load-module", autoEnable=true)
public class OModuleManagerWidget extends AbstractWidget<Void> {

    private static final Logger LOG = LoggerFactory.getLogger(OModuleManagerWidget.class);

    @Inject
    private Injector injector;

    public OModuleManagerWidget(String id, IModel<Void> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
        add(new Link<Void>("reload") {
            @Override
            public void onClick() {
                LOG.debug("Click on reload");
                reload();
                setResponsePage(new ReloadPage());
            }

            private void reload() {
                OrienteerFilter orienteerFilter = injector.getInstance(OrienteerFilter.class);
                new Reload(orienteerFilter).start();
//                OrienteerOutsideModules.unregisterModule("org.orienteer.devutils.Initializer");

            }
        });
    }

    @Override
    protected FAIcon newIcon(String id) {
        return  new FAIcon(id, FAIconType.file);
    }

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return Model.of("Loader");
    }
}
