package org.orienteer.core.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.orientechnologies.orient.core.metadata.function.OFunction;
import org.wicketstuff.select2.Response;
import org.wicketstuff.select2.TextChoiceProvider;

import java.util.Collection;
import java.util.List;

/**
 * {@link TextChoiceProvider} for {@link OFunction}s
 */
public class OFunctionTextChoiceProvider extends TextChoiceProvider<String> {
	
	public static final OFunctionTextChoiceProvider INSTANCE = new OFunctionTextChoiceProvider();
    private final List<String> functionNames;

    private OFunctionTextChoiceProvider() {
        super();
        this.functionNames = ListOFunctionNamesModel.load(null);
    }

	@Override
    public void query(final String s, int i, Response<String> response) {
		response.addAll(Collections2.filter(functionNames, (new Predicate<String>() {

            @Override
            public boolean apply(String input) {
                return input.contains(s);
            }
        })));
    }

    @Override
    public Collection<String> toChoices(Collection<String> choices) {
        return choices;
    }

    @Override
    protected String getDisplayText(String choice) {
        return choice;
    }

    @Override
    protected Object getId(String choice) {
        return choice;
    }

}
