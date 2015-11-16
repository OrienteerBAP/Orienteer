package org.orienteer.graph.model;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import ru.ydn.wicket.wicketorientdb.model.AbstractJavaSortableDataProvider;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.utils.OSchemaUtils;

/**
 * {@link SortableDataProvider} for listing of edges of the graph vertex.
 */
public class VertexEdgesDataProvider extends AbstractJavaSortableDataProvider<ODocument, String> {

    private final VertexEdgesModel vertexEdgeModel;

    public VertexEdgesDataProvider(IModel<ODocument> vertexModel) {
        this(new VertexEdgesModel(vertexModel));
    }

    public VertexEdgesDataProvider(VertexEdgesModel vertexEdgesModel) {
        super(vertexEdgesModel);
        this.vertexEdgeModel = vertexEdgesModel;
    }

    @Override
    public IModel<ODocument> model(ODocument entries) {
        return new ODocumentModel(entries);
    }


    public OClass probeOClass(int probeLimit) {
        return OSchemaUtils.probeOClass(vertexEdgeModel.getData(), probeLimit);
    }
}
