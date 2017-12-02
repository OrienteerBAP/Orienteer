package org.orienteer.graph.component.command;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.command.AbstractDeleteCommand;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.web.ODocumentPage;
import org.orienteer.graph.module.GraphModule;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import java.util.List;

/**
 * {@link Command} to delete edges
 */
public class DeleteEdgeCommand extends AbstractDeleteCommand<ODocument> implements ISecuredComponent
{
	private static final long serialVersionUID = 1L;
	private IModel<OClass> classModel;
    private IModel<ODocument> documentModel;
    
    @Inject
    private Provider<OrientGraph> orientGraphProvider;

    public DeleteEdgeCommand(OrienteerDataTable<ODocument, ?> table, IModel<ODocument> documentModel)
    {
        super(new ResourceModel("command.delete"), table);
        this.documentModel = documentModel;
        this.classModel = new OClassModel(GraphModule.EDGE_CLASS_NAME);
        setChandingModel(true);
    }
	
	@Override
	protected void performMultiAction(AjaxRequestTarget target, List<ODocument> objects) {
        OrientGraph tx = orientGraphProvider.get();
        for (ODocument doc : objects) {
            ORID id = doc.getIdentity();
            OrientEdge edge = tx.getEdge(id);
            tx.removeEdge(edge);
        }
        tx.commit();tx.begin();
        sendActionPerformed();
	}

    @Override
	public RequiredOrientResource[] getRequiredResources() {
		return OSecurityHelper.requireOClass(classModel.getObject(), OrientPermission.DELETE);
	}
    
    
    @Override
    public void detachModels() {
    	super.detachModels();
    	classModel.detach();
    	documentModel.detach();
    }

}
