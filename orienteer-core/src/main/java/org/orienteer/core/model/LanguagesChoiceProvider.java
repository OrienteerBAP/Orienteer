package org.orienteer.core.model;

import java.util.List;
import java.util.Locale;

import org.apache.wicket.util.string.Strings;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;
import org.wicketstuff.select2.StringTextChoiceProvider;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

/**
 * {@link ChoiceProvider} for combobox of language codes.
 */
public class LanguagesChoiceProvider extends StringTextChoiceProvider {
    private static final List<String> ISO_LANGUAGES = ImmutableList.copyOf(Locale.getISOLanguages());

    public static final LanguagesChoiceProvider INSTANCE = new LanguagesChoiceProvider();

    @Override
    public void query(final String term, int page, Response<String> response) {
        response.addAll(Strings.isEmpty(term)
        		
        			?ISO_LANGUAGES
        			:Collections2.filter(ISO_LANGUAGES, new Predicate<String>() {
				            @Override
				            public boolean apply(String s) {
				                return s.startsWith(term);
				            }
				        }));
    }

}
