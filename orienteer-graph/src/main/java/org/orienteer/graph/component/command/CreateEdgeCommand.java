package org.orienteer.graph.component.command;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AbstractModalWindowCommand;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.component.command.modal.SelectDialogPanel;
import org.orienteer.core.component.command.modal.SelectSubOClassDialogPage;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.graph.module.GraphModule;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import java.util.List;

/**
 * {@link Command} to create graph edge between {@link ODocument}s.
 */
public class CreateEdgeCommand extends AbstractModalWindowCommand<ODocument> implements ISecuredComponent {

    private IModel<OClass> classModel;
    private IModel<ODocument> documentModel;
    @Inject
    private Provider<OrientGraph> orientGraphProvider;

    public CreateEdgeCommand(OrienteerDataTable<ODocument, ?> table, IModel<ODocument> documentIModel) {
    	this(new ResourceModel("command.link"),table,documentIModel);
    }

    public CreateEdgeCommand(IModel<?> labelModel,OrienteerDataTable<ODocument, ?> table, IModel<ODocument> documentIModel) {
        super(labelModel, table);
        setBootstrapType(BootstrapType.SUCCESS);
        setIcon(FAIconType.plus);
        setAutoNotify(false);
        this.classModel = new OClassModel(GraphModule.VERTEX_CLASS_NAME);
        this.documentModel = documentIModel;
        setChandingModel(true);
    }

    @Override
    protected void initializeContent(final ModalWindow modal) {
        modal.setTitle(new ResourceModel("dialog.select.edge.class"));
        modal.setAutoSize(true);
        modal.setMinimalWidth(300);
        SelectSubOClassDialogPage selectEdgeClassDialog = new SelectSubOClassDialogPage(modal, new OClassModel(GraphModule.EDGE_CLASS_NAME)) {

            @Override
            protected void onSelect(AjaxRequestTarget target, OClass selectedOClass) {
                modal.setWindowClosedCallback(null);
            	modal.setTitle(new ResourceModel("dialog.select.vertices"));

                final OClassModel selectedEdgeOClassModel = new OClassModel(selectedOClass);
                OClassModel vertexOClassModel = new OClassModel(GraphModule.VERTEX_CLASS_NAME);
                modal.setContent(new SelectDialogPanel(modal.getContentId(), modal, vertexOClassModel, true) {
                    @Override
                    protected boolean onSelect(AjaxRequestTarget target, List<ODocument> objects, boolean selectMore) {
                        createEdge(objects, selectedEdgeOClassModel.getObject());
                        CreateEdgeCommand.this.sendActionPerformed();
                        return true;
                    }
                });

                modal.show(target);
            }
        };
        modal.setContent(selectEdgeClassDialog);
    }



    @Override
    public RequiredOrientResource[] getRequiredResources() {
        return OSecurityHelper.requireOClass(classModel.getObject(), OrientPermission.CREATE);
    }

    private void createEdge(List<ODocument> documents, OClass edgeClass) {
        OrientGraph tx = orientGraphProvider.get();
        for (ODocument createTo : documents) {
            tx.addEdge(null, tx.getVertex(documentModel.getObject().getIdentity()), tx.getVertex(createTo.getIdentity()), edgeClass.getName());
        }
        tx.commit();tx.begin();
    }
    
    @Override
    public void detachModels() {
    	super.detachModels();
    	classModel.detach();
    	documentModel.detach();
    }
}
