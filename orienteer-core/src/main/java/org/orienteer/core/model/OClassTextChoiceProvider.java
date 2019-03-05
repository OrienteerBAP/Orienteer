package org.orienteer.core.model;

import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.apache.wicket.model.IModel;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;
import ru.ydn.wicket.wicketorientdb.converter.OClassClassNameConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link ChoiceProvider} for {@link OClass}es
 */
public class OClassTextChoiceProvider extends ChoiceProvider<OClass> {
	
	public static final OClassTextChoiceProvider INSTANCE = new OClassTextChoiceProvider();

	private final IModel<List<OClass>> loadModel;

	public OClassTextChoiceProvider() {
	    this(new ListOClassesModel());
    }

    public OClassTextChoiceProvider(IModel<List<OClass>> loadModel) {
	    this.loadModel = loadModel;
    }

	@Override
    public void query(final String s, int i, Response<OClass> response) {
        List<OClass> classes = loadClasses(s);
        response.addAll(classes);
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

    private List<OClass> loadClasses(String query) {
        List<OClass> classes = loadModel.getObject();

        if (classes == null || classes.isEmpty()) {
            return Collections.emptyList();
        }

        if (Strings.isNullOrEmpty(query)) {
            return classes;
        }

        return loadModel.getObject().stream()
                .filter(c -> c.getName().contains(query))
                .collect(Collectors.toList());

    }
}
