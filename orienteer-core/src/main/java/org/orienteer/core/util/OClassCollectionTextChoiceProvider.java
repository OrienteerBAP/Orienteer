package org.orienteer.core.util;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.apache.wicket.model.IModel;
import org.orienteer.core.model.ListOClassesModel;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provider for Select2MultiChoice which provides {@link Collection} of {@link OClass} names
 */
public class OClassCollectionTextChoiceProvider extends ChoiceProvider<String> {

    public static final OClassCollectionTextChoiceProvider INSTANCE = new OClassCollectionTextChoiceProvider();

    private final IModel<List<OClass>> loadModel;

    public OClassCollectionTextChoiceProvider() {
        this(new ListOClassesModel());
    }

    public OClassCollectionTextChoiceProvider(IModel<List<OClass>> loadModel) {
        this.loadModel = loadModel;
    }

    @Override
    public void query(final String term, int page, Response<String> response) {
        List<String> classes = loadClasses(term);
        response.addAll(classes);
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

    private List<String> loadClasses(String query) {
        List<OClass> classes = loadModel.getObject();

        if (classes == null || classes.isEmpty()) {
            return Collections.emptyList();
        }

        if (Strings.isNullOrEmpty(query)) {
            return classes.stream().map(OClass::getName).collect(Collectors.toList());
        }

        return loadModel.getObject().stream()
                .map(OClass::getName)
                .filter(c -> c.contains(query))
                .collect(Collectors.toList());
    }
}
