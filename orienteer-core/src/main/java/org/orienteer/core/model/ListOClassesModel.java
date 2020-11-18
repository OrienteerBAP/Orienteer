package org.orienteer.core.model;

import java.util.Collection;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Ordering;
import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * {@link IModel} for {@link OClass}es which allow to sort classes by name
 */
public class ListOClassesModel extends LoadableDetachableModel<List<OClass>>
{
	private static final long serialVersionUID = 1L;
	private static final Ordering<OClass> ORDERING = Ordering.natural().nullsFirst().onResultOf(new Function<OClass, String>() {

		@Override
		public String apply(OClass input) {
			return input.getName();
		}
	});
	
	private Predicate<OClass> filter;
	
	public ListOClassesModel() {
	}
	
	public ListOClassesModel(Predicate<OClass> filter) {
		this.filter = filter;
	}
	@Override
	protected List<OClass> load() {
		return load(filter);
	}
	
	public static List<OClass> load(Predicate<OClass> filter) {
		Collection<OClass> classes = OrientDbWebSession.get().getSchema().getClasses();
		if(filter!=null) classes = Collections2.filter(classes, filter);
		return ORDERING.sortedCopy(classes);
	}
	
}