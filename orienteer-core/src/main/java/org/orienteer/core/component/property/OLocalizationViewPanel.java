package org.orienteer.core.component.property;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;

import java.util.Locale;
import java.util.Map;

/**
 * Panel for showing OLocalization embedded maps.
 * @param <V> the type of collection's objects
 */
public class OLocalizationViewPanel<V> extends GenericPanel<V> {

    public OLocalizationViewPanel(String id, IModel<ODocument> documentModel, IModel<OProperty> propertyModel) {
        super(id, new DynamicPropertyValueModel<V>(documentModel, propertyModel));
        Map<String, String> localizations = documentModel.getObject().field(propertyModel.getObject().getName());
        String currentLanguage = getLocale().getLanguage();
        String currentLanguageLocalization = localizations.get(currentLanguage);
        if (currentLanguageLocalization == null) {
            currentLanguageLocalization = localizations.get(Locale.getDefault().getLanguage());
        }

        add(new Label("currentLocalization", Strings.nullToEmpty(currentLanguageLocalization)));
    }

}
