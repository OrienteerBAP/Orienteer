package org.orienteer.graph.model;

import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import org.apache.wicket.model.IModel;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.graph.module.GraphModule;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.AbstractListModel;

import java.lang.Override;
import java.util.Collection;

/**
 * {@link IModel} to list all {@link OrientVertex}es of specific {@link OrientEdge}.
 */
public class EdgeVerticesModel extends AbstractListModel<ODocument> {

    private IModel<ODocument> edgeModel;

    public EdgeVerticesModel(IModel<ODocument> edgeModel) {
        this.edgeModel = edgeModel;
    }

    @Override
    protected Collection<ODocument> getData() {
        OrientGraph tx = OrienteerWebApplication.get().getServiceInstance(OrientGraph.class);

        try {
            if (edgeModel.getObject() != null && edgeModel.getObject().getIdentity() != null) {
                OrientEdge edge = tx.getEdge(edgeModel.getObject().getIdentity());
                if (edge == null) {
                    return Lists.newArrayList();
                }

                final ODatabaseDocument database = OrientDbWebSession.get().getDatabase();
                ODocument in = database.load(edge.getInVertex().getIdentity());
                ODocument out = database.load(edge.getOutVertex().getIdentity());
                return Lists.newArrayList(in, out);
            }

            return Lists.newArrayList();
        }
        finally {
            if (!tx.isClosed())
                tx.shutdown();
        }
    }
}
