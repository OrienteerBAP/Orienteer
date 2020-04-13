package org.orienteer.users.util;

import com.google.common.base.Strings;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.users.model.IOAuth2Provider;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ChoiceProvider} for show {@link IOAuth2Provider} as choices.
 * Shows labels as choice, but internal uses name of provider
 */
public class OAuth2ProviderChoiceProvider extends ChoiceProvider<String> {

    private final List<IOAuth2Provider> providers;

    public OAuth2ProviderChoiceProvider(List<IOAuth2Provider> providers) {
        this.providers = providers;
    }

    @Override
    public String getDisplayValue(String name) {
        return providers.stream()
                .filter(p -> Objects.equals(p.getName(), name))
                .map(this::createLabelModel)
                .map(IModel::getObject)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Can't find provider for name: " + name));
    }

    @Override
    public String getIdValue(String label) {
        return label;
    }

    @Override
    public void query(String term, int page, Response<String> response) {
        providers.stream()
                .map(p -> {
                    if (Strings.isNullOrEmpty(term)) {
                        return p.getName();
                    }

                    String name = p.getName().toLowerCase();
                    String label = createLabelModel(p).getObject();
                    label = label.toLowerCase();

                    if (name.contains(term.toLowerCase()) || label.contains(term.toLowerCase())) {
                        return p.getName();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .forEach(response::add);
    }

    @Override
    public Collection<String> toChoices(Collection<String> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        return providers.stream()
                .filter(p -> ids.contains(p.getName()))
                .map(IOAuth2Provider::getName)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    protected IModel<String> createLabelModel(IOAuth2Provider provider) {
        return new ResourceModel(provider.getLabel());
    }
}
