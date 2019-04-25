package org.orienteer.users.component.visualizer;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.visualizer.AbstractSimpleVisualizer;
import org.orienteer.users.model.IOAuth2Provider;
import org.orienteer.users.model.OAuth2Provider;
import org.orienteer.users.util.OAuth2ProviderChoiceProvider;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Select2Choice;

import java.util.Arrays;
import java.util.List;

/**
 * Implementation of {@link org.orienteer.core.component.visualizer.IVisualizer} for visualize OAuth2 providers
 * EDIT: show {@link Select2Choice} with providers labels
 * VIEW: show {@link Label} with provider label
 */
public class OAuth2ProviderVisualizer extends AbstractSimpleVisualizer {

    public static final String NAME = "oauth2-visualizer";


    public OAuth2ProviderVisualizer() {
        this(NAME);
    }

    protected OAuth2ProviderVisualizer(String name) {
        super(name, false, OType.STRING);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> Component createComponent(String id,
                                         DisplayMode mode,
                                         IModel<ODocument> documentModel,
                                         IModel<OProperty> propertyModel,
                                         IModel<V> valueModel) {
        if (mode == DisplayMode.EDIT) {
            return new Select2Choice<>(id, (IModel<String>) valueModel, createProvider());
        }

        return new Label(id, valueModel) {
            @Override
            public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
                String name = (String) getDefaultModelObject();
                IOAuth2Provider provider = getProviderByName(name);
                String label = createProviderLabelModel(provider).getObject();
                replaceComponentTagBody(markupStream, openTag, label);
            }
        };
    }

    /**
     * @return {@link ChoiceProvider} which provides provider label
     */
    protected ChoiceProvider<String> createProvider() {
        List<IOAuth2Provider> providers = Arrays.asList(OAuth2Provider.values());
        return new OAuth2ProviderChoiceProvider(providers);
    }

    /**
     * @param name name of provider
     * @return {@link IOAuth2Provider} with given name. By default try to find enum {@link OAuth2Provider}
     */
    protected IOAuth2Provider getProviderByName(String name) {
        return OAuth2Provider.valueOf(name);
    }

    /**
     * @param provider {@link IOAuth2Provider}
     * @return label of provider
     */
    protected IModel<String> createProviderLabelModel(IOAuth2Provider provider) {
        return new ResourceModel(provider.getLabel());
    }
}
