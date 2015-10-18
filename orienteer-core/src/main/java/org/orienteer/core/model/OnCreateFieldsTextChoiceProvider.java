package org.orienteer.core.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.wicketstuff.select2.Response;
import org.wicketstuff.select2.TextChoiceProvider;

/**
 * {@link TextChoiceProvider} for "onCreate.fields" multiSelect.
 */
public class OnCreateFieldsTextChoiceProvider extends TextChoiceProvider<String> {

    private static final List<String> ON_CREATE_FIELDS_SELECTIONS =
            Arrays.asList(new String[]{ "_allow", "_allowRead", "_allowWrite", "_allowDelete" });

    public static final OnCreateFieldsTextChoiceProvider INSTANCE = new OnCreateFieldsTextChoiceProvider();

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
            response.addAll(Collections2.filter(ON_CREATE_FIELDS_SELECTIONS, new Predicate<String>() {
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
