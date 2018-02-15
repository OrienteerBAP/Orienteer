package org.orienteer.core.util;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.Collection;

/**
 * Provider for Select2MultiChoice which provides {@link Collection} of {@link OClass} names
 */
public class OClassCollectionTextChoiceProvider extends ChoiceProvider<String> {

    public static final OClassCollectionTextChoiceProvider INSTANCE = new OClassCollectionTextChoiceProvider();

    @Override
    public void query(final String term, int page, Response<String> response) {
        response.addAll(new DBClosure<Collection<String>>() {
            @Override
            protected Collection<String> execute(ODatabaseDocument db) {
                Collection<OClass> classes = db.getMetadata().getSchema().getClasses();
                Predicate<OClass> filter = new Predicate<OClass>() {
                    @Override
                    public boolean apply(OClass input) {
                        return Strings.isNullOrEmpty(term) || input.getName().contains(term);
                    }
                };
                return Collections2.transform(Collections2.filter(classes, filter), new Function<OClass, String>() {
                    @Override
                    public String apply(OClass input) {
                        return input.getName();
                    }
                });
            }
        }.execute());
    }

    @Override
    public String getDisplayValue(String choice) {
        return choice;
    }

    @Override
    public String getIdValue(String choice) {
        return choice;
    }

    @Override
    public Collection<String> toChoices(Collection<String> ids) {
        return ids;
    }
}
