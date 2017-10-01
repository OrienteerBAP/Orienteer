package org.orienteer.core.component;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.orientechnologies.orient.core.metadata.security.ORule;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.core.boot.loader.OrienteerClassLoader;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactField;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OArtifactColumn;
import org.orienteer.core.component.table.component.GenericTablePanel;
import ru.ydn.wicket.wicketorientdb.model.JavaSortableDataProvider;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

import java.util.List;
import java.util.Set;

/**
 * Panel which shows modal window with disabled modules after reloading Orienteer.
 * Panel shows only once if user has permissions for manage Orienteer modules. (Permission EXECUTE on SCHEMA)
 */
public class OModulesLoadFailedPanel extends Panel {
    private static Set<String> showsForUsers = Sets.newHashSet();

    public OModulesLoadFailedPanel(String id) {
        super(id);
        final ModalWindow modal = new ModalWindow("modal");
        modal.setTitle(new ResourceModel("application.load.feedback.title.error"));
        modal.setContent(newContent(modal.getContentId()));
        modal.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            @Override
            public void onClose(AjaxRequestTarget target) {
                showsForUsers.add(OrienteerWebSession.get().getUsername());
            }
        });
        add(new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                modal.show(target);
            }

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                response.render(OnLoadHeaderItem.forScript(String.format("(%s)();", getCallbackFunction())));
            }
        });
        add(modal);
    }

    private Component newContent(String id) {
        IModel<Set<OArtifact>> modules = OrienteerClassLoader.getDisabledModules();
        return new GenericTablePanel<>(id, getColumns(),
                new JavaSortableDataProvider<OArtifact, String>(modules), 20);
    }

    private List<IColumn<OArtifact, String>> getColumns() {
        List<IColumn<OArtifact, String>> columns = Lists.newArrayList();
        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        columns.add(new OArtifactColumn(OArtifactField.GROUP.asModel(), modeModel));
        columns.add(new OArtifactColumn(OArtifactField.ARTIFACT.asModel(), modeModel));
        columns.add(new OArtifactColumn(OArtifactField.VERSION.asModel(), modeModel));
        columns.add(new OArtifactColumn(OArtifactField.DESCRIPTION.asModel(), modeModel));
        columns.add(new OArtifactColumn(OArtifactField.TRUSTED.asModel(), modeModel));
        return columns;
    }

    @Override
    public boolean isEnabled() {
        if (!OrienteerWebSession.get().isSignedIn() ||
                !OSecurityHelper.isAllowed(ORule.ResourceGeneric.SCHEMA, "", OrientPermission.EXECUTE))
            return false;
        return OrienteerClassLoader.getDisabledModules().getObject().size() > 0 && !showsForUsers.contains(OrienteerWebSession.get().getUsername());
    }

    @Override
    public boolean isVisible() {
        return isEnabled();
    }

    /**
     * Clear info about users which saw this panel
     */
    public static void clearInfoAboutUsers() {
        showsForUsers.clear();
    }
}
