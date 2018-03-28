package org.orienteer.core.web;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.danekja.java.util.function.serializable.SerializablePredicate;
import org.danekja.java.util.function.serializable.SerializableSupplier;
import org.orienteer.core.MountPath;
import org.orienteer.core.component.OClassSearchPanel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page to search and display search results
 */
@MountPath("/search")
public class SearchPage extends OrienteerBasePage<String> {

	private SerializablePredicate<OClass> predicate;
	
	public SearchPage() {
		super(Model.of(""));
	}

	public SearchPage(IModel<String> model) {
		super(model);
	}

	public SearchPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	protected IModel<String> resolveByPageParameters(PageParameters params) {
		String query = params.get("q").toOptionalString();
		return Model.of(query);
	}

	@Override
	public void initialize() {
		super.initialize();
		this.predicate = createPredicate();
		add(new OClassSearchPanel("searchPanel", getModel(), createClassesGetter()));
	}

	private SerializableSupplier<List<OClass>> createClassesGetter() {
		return () -> getDatabase().getMetadata().getSchema().getClasses().stream()
				.filter(predicate)
				.collect(Collectors.toList());
	}

	private SerializablePredicate<OClass> createPredicate() {
		return c -> true;
	}

	@Override
	public IModel<String> getTitleModel() {
		return new ResourceModel("search.title");
	}

}
