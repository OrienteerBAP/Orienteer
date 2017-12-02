package org.orienteer.graph.component.command;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
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
 * {@link Command} to delete vertices
 */
public class DeleteVertexCommand extends AbstractDeleteCommand<ODocument> implements ISecuredComponent
{
	private static final long serialVersionUID = 1L;
    private final IModel<ODocument> documentModel;
    private IModel<OClass> classModel;

    @Inject
    private Provider<OrientGraph> orientGraphProvider;


	public DeleteVertexCommand(OrienteerDataTable<ODocument, ?> table, IModel<ODocument> documentModel)
	{
		super(table);
		this.classModel = new OClassModel(GraphModule.VERTEX_CLASS_NAME);
        this.documentModel = documentModel;
        setChandingModel(true);
	}
	
	@Override
	protected void performMultiAction(AjaxRequestTarget target, List<ODocument> objects) {
		super.performMultiAction(target, objects);
        OrientGraph tx = orientGraphProvider.get();
        for (ODocument doc : objects) {
            ORID id = doc.getIdentity();
            tx.removeVertex(tx.getVertex(id));
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
