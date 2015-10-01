package org.orienteer.graph.model;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import ru.ydn.wicket.wicketorientdb.model.AbstractJavaSortableDataProvider;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;

/**
 * {@link SortableDataProvider} for listing of edges of the graph vertex.
 */
public class VertexEdgesDataProvider extends AbstractJavaSortableDataProvider<ODocument, String> {

    public VertexEdgesDataProvider(IModel<ODocument> vertexModel) {
        super(new VertexEdgesModel(vertexModel));
    }

    @Override
    public IModel<ODocument> model(ODocument entries) {
        return new ODocumentModel(entries);
    }
}
