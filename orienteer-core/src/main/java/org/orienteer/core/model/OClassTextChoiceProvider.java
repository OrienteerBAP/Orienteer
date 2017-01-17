package org.orienteer.core.model;

import java.util.Collection;

import org.apache.wicket.util.string.Strings;
import org.wicketstuff.select2.Response;
import org.wicketstuff.select2.ChoiceProvider;

import ru.ydn.wicket.wicketorientdb.utils.OClassClassNameConverter;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * {@link ChoiceProvider} for {@link OClass}es
 */
public class OClassTextChoiceProvider extends ChoiceProvider<OClass> {
	
	public static final OClassTextChoiceProvider INSTANCE = new OClassTextChoiceProvider();

	@Override
    public void query(final String s, int i, Response<OClass> response) {
		if(!Strings.isEmpty(s)) {
			response.addAll(ListOClassesModel.load(new Predicate<OClass>() {
				
				@Override
				public boolean apply(OClass input) {
					return input.getName().contains(s);
				}
			}));
		}
    }

    @Override
    public Collection<OClass> toChoices(Collection<String> collection) {
        return Collections2.transform(collection, OClassClassNameConverter.INSTANCE.reverse());
    }

	@Override
    public String getDisplayValue(OClass choice) {
        return choice.getName();
    }

    @Override
    public String getIdValue(OClass choice) {
        return choice.getName();
    }

}
