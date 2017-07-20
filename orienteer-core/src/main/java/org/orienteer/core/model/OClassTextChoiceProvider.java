package org.orienteer.core.model;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;
import ru.ydn.wicket.wicketorientdb.converter.OClassClassNameConverter;

import java.util.Collection;

/**
 * {@link ChoiceProvider} for {@link OClass}es
 */
public class OClassTextChoiceProvider extends ChoiceProvider<OClass> {
	
	public static final OClassTextChoiceProvider INSTANCE = new OClassTextChoiceProvider();

	@Override
    public void query(final String s, int i, Response<OClass> response) {
        response.addAll(ListOClassesModel.load(new Predicate<OClass>() {
				
            @Override
            public boolean apply(OClass input) {
                return Strings.isNullOrEmpty(s) || input.getName().contains(s);
            }
        }));
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
