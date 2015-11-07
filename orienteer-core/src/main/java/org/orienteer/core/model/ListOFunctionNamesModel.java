package org.orienteer.core.model;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.function.OFunction;
import com.orientechnologies.orient.core.metadata.function.OFunctionLibrary;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * {@link IModel} for {@link OFunction}s which allow to sort functions by name
 */
public class ListOFunctionNamesModel extends LoadableDetachableModel<List<String>>
{
	private static final long serialVersionUID = 1L;
	private static final Ordering<String> ORDERING = Ordering.natural().nullsFirst().onResultOf(new Function<String, String>() {

		@Override
		public String apply(String name) {
			return name;
		}
	});

	private Predicate<String> filter;

	public ListOFunctionNamesModel() {
	}

	public ListOFunctionNamesModel(Predicate<String> filter) {
		this.filter = filter;
	}
	@Override
	protected List<String> load() {
		return load(filter);
	}
	
	public static List<String> load(Predicate<String> filter) {

        ODatabaseDocumentInternal database = (ODatabaseDocumentInternal) OrientDbWebSession.get().getDatabase();
        List<ODocument> result = database.query(new OSQLSynchQuery<ODocument>("select from OFunction order by name"));
        Collection<String> functionNames = Lists.newArrayList(Lists.transform(result, new Function<ODocument, String>() {

            @Override
            public String apply(ODocument input) {
                return new OFunction(input).getName();
            }
        }));
		if(filter!=null) functionNames = Collections2.filter(functionNames, filter);
		return ORDERING.sortedCopy(functionNames);
	}
	
}