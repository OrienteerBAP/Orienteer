package org.orienteer.core.model;

import java.util.Collection;

import org.wicketstuff.select2.Response;
import org.wicketstuff.select2.TextChoiceProvider;

import ru.ydn.wicket.wicketorientdb.utils.OClassClassNameConverter;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * {@link TextChoiceProvider} for {@link OClass}es
 */
public class OClassTextChoiceProvider extends TextChoiceProvider<OClass> {
	
	public static final OClassTextChoiceProvider INSTANCE = new OClassTextChoiceProvider();

	@Override
    public void query(final String s, int i, Response<OClass> response) {
		response.addAll(ListOClassesModel.load(new Predicate<OClass>() {
			
			@Override
			public boolean apply(OClass input) {
				return input.getName().contains(s);
			}
		}));
    }

    @Override
    public Collection<OClass> toChoices(Collection<String> collection) {
        return Collections2.transform(collection, OClassClassNameConverter.INSTANCE.reverse());
    }

    @Override
    protected String getDisplayText(OClass choice) {
        return choice.getName();
    }

    @Override
    protected Object getId(OClass choice) {
        return choice.getName();
    }

}
