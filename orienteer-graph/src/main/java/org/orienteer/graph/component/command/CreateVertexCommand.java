package org.orienteer.graph.component.command;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AbstractModalWindowCommand;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.component.command.modal.SelectSubOClassDialogPage;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.web.ODocumentPage;
import org.orienteer.graph.module.GraphModule;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

/**
 * {@link Command} to create neighbour vertex
 */
public class CreateVertexCommand extends AbstractModalWindowCommand<ODocument> implements ISecuredComponent {

    private IModel<OClass> classModel;
    private IModel<ODocument> documentModel;

    public CreateVertexCommand(OrienteerDataTable<ODocument, ?> table, IModel<ODocument> documentIModel) {
        super(new ResourceModel("command.create"), table);
        setBootstrapType(BootstrapType.PRIMARY);
        setIcon(FAIconType.plus);
        setAutoNotify(false);
        this.classModel = new OClassModel(GraphModule.VERTEX_CLASS_NAME);
        this.documentModel = documentIModel;
    }

    @Override
    protected void initializeContent(final ModalWindow modal) {
        modal.setTitle(new ResourceModel("dialog.select.vertex.class"));
        modal.setAutoSize(true);
        modal.setMinimalWidth(300);
        SelectSubOClassDialogPage selectVertexClassDialog = new SelectSubOClassDialogPage(modal, new OClassModel(GraphModule.VERTEX_CLASS_NAME)) {

            @Override
            protected void onSelect(AjaxRequestTarget target, final OClass selectedOVertexClass) {
                modal.setTitle(new ResourceModel("dialog.select.edge.class"));

                OClassModel edgeOClassModel = new OClassModel(GraphModule.EDGE_CLASS_NAME);
                modal.setContent(new SelectSubOClassDialogPage(modal, edgeOClassModel) {
                    @Override
                    protected void onSelect(AjaxRequestTarget target, final OClass selectedOEdgeClass) {
                        createVertex(selectedOVertexClass, selectedOEdgeClass);

                        modal.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
                        {
                            private static final long serialVersionUID = 1L;

                            @Override
                            public void onClose(AjaxRequestTarget target)
                            {
                                setResponsePage(new ODocumentPage(new ODocumentModel(documentModel.getObject())).setModeObject(DisplayMode.VIEW));
                                //target.add(getPage());
                            }
                        });
                    }
                });

                modal.show(target);

            }
        };
        modal.setContent(selectVertexClassDialog);
    }

    @Override
    public RequiredOrientResource[] getRequiredResources() {
        return OSecurityHelper.requireOClass(classModel.getObject(), OrientPermission.CREATE);
    }

    private void createVertex(OClass vertexClass, OClass edgeClass) {
        OrientGraph tx = new OrientGraphFactory(getDatabase().getURL()).getTx();
        OrientVertex newVertex = tx.addVertex(vertexClass.getName(), (String) null);
        OrientVertex vertex = tx.getVertex(documentModel.getObject().getIdentity());
        tx.addEdge(null, vertex, newVertex, edgeClass.getName());
    }
    
    @Override
    public void detachModels() {
    	super.detachModels();
    	classModel.detach();
    	documentModel.detach();
    }    
}
