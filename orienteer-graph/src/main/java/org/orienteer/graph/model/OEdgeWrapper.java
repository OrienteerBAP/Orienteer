package org.orienteer.graph.model;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import org.orienteer.core.OrienteerWebApplication;

/**
 * {@link ODocumentWrapper} for edges 
 */
public class OEdgeWrapper extends ODocumentWrapper {

    private static final long serialVersionUID = -4305050736055035970L;

    public static final String CLASS_NAME = "E";

    @Inject
    private transient Provider<OrientGraph> grapthProvider;
    
    private transient OrientEdge edge;

    public OEdgeWrapper() {
        super(CLASS_NAME);
    }

    protected OEdgeWrapper(String iClassName) {
        super(iClassName);
    }

    public OEdgeWrapper(ODocument iDocument) {
        super(iDocument);
    }

    public OrientEdge getEdge() {
        if(edge == null) {
            edge = getGraph().getEdge(getDocument());
        }
        return edge;
    }

    public OrientGraph getGraph() {
    	if(grapthProvider==null) {
    		OrienteerWebApplication.get().getInjector().injectMembers(this);
    	}
    	return grapthProvider.get();
    }
}
