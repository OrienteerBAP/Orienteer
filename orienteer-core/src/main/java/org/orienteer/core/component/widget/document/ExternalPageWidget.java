package org.orienteer.core.component.widget.document;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.InlineFrame;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.string.interpolator.MapVariableInterpolator;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.model.ODocumentMapWrapper;

import static org.orienteer.core.module.OWidgetsModule.OPROPERTY_PAGE_URL;
import static org.orienteer.core.module.OWidgetsModule.OPROPERTY_STYLE;


/**
 * Widget to embed external web-page.
 */
@Widget(domain="document", oClass = "ExternalPageWidget", id = ExternalPageWidget.WIDGET_TYPE_ID, autoEnable=false)
public class ExternalPageWidget extends AbstractWidget<ODocument> {

    public static final String WIDGET_OCLASS_NAME = "ExternalPageWidget";
    public static final String WIDGET_TYPE_ID = "external_page";
    private String externalPageUrl;
    private String style;

    public ExternalPageWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.external_link);
    }

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return MapVariableInterpolator.interpolate(externalPageUrl, new ODocumentMapWrapper(getModelObject()));
            }
        };
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        if (!Strings.isNullOrEmpty(externalPageUrl)) {
            String interpolatedUrl = MapVariableInterpolator.interpolate(externalPageUrl, new ODocumentMapWrapper(getModelObject()));
            RedirectPage page = new RedirectPage(interpolatedUrl);
            final InlineFrame frame = new InlineFrame("embeddedPage", page);
            frame.add(new AttributeModifier("style", style));
            add(frame);
        } else {
            add(new EmptyPanel("embeddedPage"));
        }
    }

    @Override
    public void loadSettings() {
        super.loadSettings();

        ODocument doc = getWidgetDocument();
        if(doc==null) return;

        externalPageUrl = (String)MoreObjects.firstNonNull(doc.field(OPROPERTY_PAGE_URL), "");
        style = (String)MoreObjects.firstNonNull(doc.field(OPROPERTY_STYLE), "");
    }

    @Override
    public void saveSettings() {
        super.saveSettings();

        ODocument doc = getWidgetDocument();
        if(doc==null) return;
        getDashboardPanel().getDashboardSupport().saveSettings(this, doc);
        doc.field(OPROPERTY_PAGE_URL, externalPageUrl);
        doc.field(OPROPERTY_STYLE, style);
    }
}
