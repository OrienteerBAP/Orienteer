package org.orienteer.graph.model;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import org.orienteer.core.OrienteerWebApplication;

/**
 * {@link ODocumentWrapper} for vertexes 
 */
public class OVertexWrapper extends ODocumentWrapper {

    private static final long serialVersionUID = -201404934600341062L;

    public static final String CLASS_NAME = "V";

    @Inject
    private transient Provider<OrientGraph> grapthProvider;
    
    private transient OrientVertex vertex;

    public OVertexWrapper() {
        super(CLASS_NAME);
    }

    protected OVertexWrapper(String iClassName) {
        super(iClassName);
    }

    public OVertexWrapper(ODocument iDocument) {
        super(iDocument);
    }

    public OrientVertex getVertex() {
        if(vertex == null) {
            vertex = getGraph().getVertex(getDocument());
        }
        return vertex;
    }

    public OrientGraph getGraph() {
    	if(grapthProvider==null) {
    		OrienteerWebApplication.get().getInjector().injectMembers(this);
    	}
    	return grapthProvider.get();
    }
}
