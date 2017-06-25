package org.orienteer.core.component.command;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.widget.loader.OArtifactsModalWindowPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add Orienteers module command. Show modal window and behavior of closing modal window.
 */
public class AddOArtifactCommand extends AbstractModalWindowCommand<OArtifact> {

    private final OrienteerDataTable<OArtifact, ?> table;
    private final OArtifactsModalWindowPage modalWindowPage;

    private static final String ADD_BUT            = "command.add";
    private static final String MODAL_WINDOW_TITLE = "widget.artifacts.modal.window.title";

    private static final Logger LOG = LoggerFactory.getLogger(AddOArtifactCommand.class);

    public AddOArtifactCommand(OrienteerDataTable<OArtifact, ?> table, OArtifactsModalWindowPage page) {
        super(new ResourceModel(ADD_BUT), table);
        this.modalWindowPage = page;
        this.table = table;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initializeContent(ModalWindow modal) {
        modal.setOutputMarkupPlaceholderTag(true);
        modal.setTitle(new ResourceModel(MODAL_WINDOW_TITLE));

        modal.setPageCreator(new ModalWindow.PageCreator() {
            @Override
            public Page createPage() {
                return modalWindowPage;
            }
        });

        modal.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            @Override
            public void onClose(AjaxRequestTarget target) {
                target.add(table);
            }
        });

        modal.setAutoSize(true);
        modal.setMinimalWidth(800);
        modal.setMinimalHeight(600);
        modalWindowPage.setModalWindow(modal);
    }

    @Override
    protected void onInstantiation() {
        super.onInstantiation();
        setIcon(FAIconType.plus);
        setBootstrapType(BootstrapType.PRIMARY);
        setChangingDisplayMode(true);
    }

}
