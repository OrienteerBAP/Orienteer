package org.orienteer.core.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.wicketstuff.select2.Response;
import org.wicketstuff.select2.TextChoiceProvider;

/**
 * {@link com.vaynberg.wicket.select2.ChoiceProvider} for combobox of language codes.
 */
public class LanguagesChoiceProvider extends TextChoiceProvider<String> {
    private static final List<String> ISO_LANGUAGES = ImmutableList.copyOf(Locale.getISOLanguages());

    public static final LanguagesChoiceProvider INSTANCE = new LanguagesChoiceProvider();

    @Override
    protected String getDisplayText(String choice) {
        return choice;
    }

    @Override
    protected Object getId(String choice) {
        return choice;
    }

    @Override
    public void query(final String term, int page, Response<String> response) {
        response.addAll(Collections2.filter(ISO_LANGUAGES, new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return s.contains(term);
            }
        }));
    }

    @Override
    public Collection<String> toChoices(Collection<String> ids) {
        return ids;
    }
}
