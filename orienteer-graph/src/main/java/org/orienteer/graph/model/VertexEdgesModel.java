package org.orienteer.graph.model;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import org.apache.wicket.model.IModel;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.graph.module.GraphModule;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.AbstractListModel;

import java.util.Collection;

/**
 * {@link IModel} to list all {@link OrientEdge}s of specific {@link OrientVertex}.
 */
public class VertexEdgesModel extends AbstractListModel<ODocument> {

    private IModel<ODocument> vertexModel;

    public VertexEdgesModel(IModel<ODocument> vertexModel) {
        this.vertexModel = vertexModel;
    }

    @Override
    protected Collection<ODocument> getData() {
        OrientGraph tx = OrienteerWebApplication.get().getServiceInstance(OrientGraph.class);

        try {
            if (vertexModel.getObject() != null && vertexModel.getObject().getIdentity() != null) {
                OrientVertex vertex = tx.getVertex(vertexModel.getObject().getIdentity());
                if (vertex == null) {
                    return Lists.newArrayList();
                }

                Iterable<Edge> edges = vertex.getEdges(Direction.BOTH);
                return Lists.newArrayList(Iterables.transform(edges, new Function<Edge, ODocument>() {
                    @Override
                    public ODocument apply(Edge edge) {
                        return OrientDbWebSession.get().getDatabase().load(((OrientEdge) edge).getRecord());
                    }
                }));
            } else {
                return Lists.newArrayList();
            }
        } finally {
            tx.shutdown();
        }
    }
}
