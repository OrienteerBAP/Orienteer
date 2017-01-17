package org.orienteer.core.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.util.string.Strings;
import org.wicketstuff.select2.Response;
import org.wicketstuff.select2.StringTextChoiceProvider;

/**
 * {@link StringTextChoiceProvider} for "onCreate.fields" multiSelect.
 */
public class OnCreateFieldsTextChoiceProvider extends StringTextChoiceProvider {

    private static final List<String> ON_CREATE_FIELDS_SELECTIONS =
            Arrays.asList(new String[]{ "_allow", "_allowRead", "_allowWrite", "_allowDelete" });

    public static final OnCreateFieldsTextChoiceProvider INSTANCE = new OnCreateFieldsTextChoiceProvider();

    @Override
    public void query(final String term, int page, Response<String> response) {
            response.addAll(Collections2.filter(ON_CREATE_FIELDS_SELECTIONS, new Predicate<String>() {
                @Override
                public boolean apply(String s) {
                    return Strings.isEmpty(term) || s.contains(term);
                }
            }));
    }

}
