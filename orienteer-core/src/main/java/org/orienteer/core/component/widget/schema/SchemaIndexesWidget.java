package org.orienteer.core.component.widget.schema;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.widget.AbstractIndexesWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.AbstractListModel;
import ru.ydn.wicket.wicketorientdb.model.OIndexesDataProvider;

import java.util.Collection;

/**
 * Widget to show and modify all schema {@link OIndex}es
 */
@Widget(id="schema-indexes", domain="schema", tab="indexes", order=30, autoEnable=true)
public class SchemaIndexesWidget extends AbstractIndexesWidget<Void> {

	public SchemaIndexesWidget(String id, IModel<Void> model,
							   IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
	}

	@Override
	protected String getCaptionResourceKey() {
		return "schema.all.indexes";
	}

	@Override
	protected OIndexesDataProvider getIndexDataProvider() {
		AbstractListModel<OIndex> allIndexesModel = new AbstractListModel<OIndex>() {
			@Override
			@SuppressWarnings("unchecked")
			protected Collection<OIndex> getData() {
				ODatabaseDocumentInternal db = OrientDbWebSession.get().getDatabaseDocumentInternal();
				return (Collection<OIndex>) db.getMetadata().getIndexManagerInternal().getIndexes(db);
			}
		};

		return new OIndexesDataProvider(allIndexesModel);
	}
}
