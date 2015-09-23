package org.orienteer.graph.model;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;import ru.ydn.wicket.wicketorientdb.model.AbstractJavaSortableDataProvider;import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;import java.lang.Override;

/**
 * {@link SortableDataProvider} for listing of vertices of the graph edge.
 */
public class EdgeVerticesDataProvider extends AbstractJavaSortableDataProvider<ODocument, String> {

    public EdgeVerticesDataProvider(IModel<ODocument> vertexModel) {
        super(getEdgeVerticesModel(vertexModel));
    }

    public EdgeVerticesDataProvider(EdgeVerticesModel edgeVerticesModel) {
        super(edgeVerticesModel);
    }

    private static EdgeVerticesModel getEdgeVerticesModel(IModel<ODocument> vertexModel) {
        return new EdgeVerticesModel(vertexModel);
    }

    @Override
    public IModel<ODocument> model(ODocument entries) {
        return new ODocumentModel(entries);
    }
}
