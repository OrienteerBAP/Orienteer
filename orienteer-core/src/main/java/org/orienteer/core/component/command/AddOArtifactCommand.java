package org.orienteer.core.component.command;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.widget.loader.OArtifactsModalWindowPage;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

/**
 * Add Orienteers module command. Show modal window and behavior of closing modal window.
 */
@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions = OrientPermission.EXECUTE)
public class AddOArtifactCommand extends AbstractModalWindowCommand<OArtifact> {

    private final OrienteerDataTable<OArtifact, ?> table;
    private final OArtifactsModalWindowPage modalWindowPage;

    private static final String ADD_BUT            = "command.add";
    private static final String MODAL_WINDOW_TITLE = "widget.artifacts.modal.window.title";


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

        modal.setPageCreator((ModalWindow.PageCreator) () -> modalWindowPage);

        modal.setWindowClosedCallback((ModalWindow.WindowClosedCallback) target -> target.add(table));

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
