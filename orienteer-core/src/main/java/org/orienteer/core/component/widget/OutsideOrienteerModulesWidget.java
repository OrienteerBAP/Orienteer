package org.orienteer.core.component.widget;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.service.OrienteerFilter;
import org.orienteer.core.web.OrienteerReloadPage;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vitaliy Gonchar
 */
@Widget(id="reload-orienteer", domain="schema", autoEnable = true)
public class OutsideOrienteerModulesWidget extends AbstractModeAwareWidget<Void> {

    private static final Logger LOG = LoggerFactory.getLogger(OutsideOrienteerModulesWidget.class);

    public OutsideOrienteerModulesWidget(String id, IModel<Void> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
        add(new Link<Void>("reload") {
            @Override
            public void onClick() {
                setResponsePage(new OrienteerReloadPage());
                OrienteerFilter.reloadOrienteer();
            }
        });
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.recycle);
    }

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return  new ResourceModel("loader.title");
    }
}
