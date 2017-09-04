package org.orienteer.core.component.meta;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactField;
import org.orienteer.core.component.property.AbstractFileUploadPanel;
import org.orienteer.core.component.property.BooleanEditPanel;
import org.orienteer.core.component.property.BooleanViewPanel;
import org.orienteer.core.component.property.DisplayMode;

import java.util.List;

/**
 * Meta panel for {@link OArtifact}
 * @param <V> type of value
 */
public class OArtifactMetaPanel<V> extends AbstractComplexModeMetaPanel<OArtifact, DisplayMode, OArtifactField, V> {

    private static final String GROUP       = "widget.artifacts.group";
    private static final String ARTIFACT    = "widget.artifacts.artifact";
    private static final String VERSION     = "widget.artifacts.version";
    private static final String REPOSITORY  = "widget.artifacts.repository";
    private static final String DESCRIPTION = "widget.artifacts.description";
    private static final String FILE        = "widget.artifacts.file";
    private static final String LOAD        = "widget.artifacts.load";
    private static final String TRUSTED     = "widget.artifacts.trusted";

    private Component componentForUpdate;

    public OArtifactMetaPanel(String id, IModel<DisplayMode> modeModel,
                                         IModel<OArtifact> entityModel, IModel<OArtifactField> criteryModel) {
        super(id, modeModel, entityModel, criteryModel);
        setOutputMarkupId(true);
    }


    @Override
    @SuppressWarnings("unchecked")
    protected V getValue(OArtifact oArtifact, OArtifactField critery) {
        V value = null;
        switch (critery) {
            case GROUP:
                value = (V) oArtifact.getArtifactReference().getGroupId();
                break;
            case ARTIFACT:
                value = (V) oArtifact.getArtifactReference().getArtifactId();
                break;
            case VERSION:
                value = (V) oArtifact.getArtifactReference().getVersion();
                break;
            case DESCRIPTION:
                value = (V) oArtifact.getArtifactReference().getDescription();
                break;
            case DOWNLOADED:
                value = (V) Boolean.valueOf(oArtifact.isDownloaded());
                break;
            case LOAD:
                value = (V) Boolean.valueOf(oArtifact.isLoad());
                break;
            case TRUSTED:
                value = (V) Boolean.valueOf(oArtifact.isTrusted());
                break;
        }
        return value;
    }

    @Override
    protected void setValue(OArtifact oArtifact, OArtifactField critery, V value) {

        if (oArtifact.isDownloaded())
            return;
        switch (critery) {
            case GROUP:
                oArtifact.getArtifactReference().setGroupId((String) value);
                break;
            case ARTIFACT:
                oArtifact.getArtifactReference().setArtifactId((String) value);
                break;
            case VERSION:
                oArtifact.getArtifactReference().setVersion((String) value);
                break;
            case REPOSITORY:
                oArtifact.getArtifactReference().setRepository((String) value);
                break;
            case DESCRIPTION:
                oArtifact.getArtifactReference().setDescription((String) value);
                break;
            case LOAD:
                oArtifact.setLoad((Boolean) value);
                break;
            case TRUSTED:
                oArtifact.setTrusted((Boolean) value);
                break;
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    protected Component resolveComponent(String id, DisplayMode mode, OArtifactField critery) {
        Component result = null;
        IModel<V> model = getModel();
        if (DisplayMode.EDIT.equals(mode)) {
            switch (critery) {
                case GROUP:
                    result = new TextField<>(id, model);
                    break;
                case ARTIFACT:
                    result = new TextField<>(id, model);
                    break;
                case VERSION:
                    List<String> versions = getEntityModel().getObject().getArtifactReference().getAvailableVersions();
                    if (versions != null && !versions.isEmpty()) {
                        DropDownChoice<String> choice = new DropDownChoice<>(id, (IModel<String>) model, versions);
                        choice.getModel().setObject(versions.get(0));
                        result = choice;
                    } else result = new TextField<>(id, model);
                    break;
                case REPOSITORY:
                    result = new TextField<>(id, model);
                    break;
                case DESCRIPTION:
                    result = new TextArea<>(id, model);
                    break;
                case DOWNLOADED:
                    result = new BooleanViewPanel(id, (IModel<Boolean>) model);
                    break;
                case LOAD:
                    result = new BooleanEditPanel(id, (IModel<Boolean>) model);
                    break;
                case TRUSTED:
                    result = new BooleanEditPanel(id, (IModel<Boolean>) model);
                    break;
                case FILE:
                    result = new AbstractFileUploadPanel(id) {
                        @Override
                        protected void configureFileUploadField(FileUploadField uploadField) {
                            configureJarFileUploadField(uploadField);
                        }
                    };
            }
        } else {
            switch (critery) {
                case GROUP:
                    result = new Label(id, model);
                    break;
                case ARTIFACT:
                    result = new Label(id, model);
                    break;
                case VERSION:
                    result = new Label(id, model);
                    break;
                case REPOSITORY:
                    result = new Label(id, model);
                    break;
                case DESCRIPTION:
                    result = new MultiLineLabel(id, model);
                    break;
                case DOWNLOADED:
                    result = new BooleanViewPanel(id, (IModel<Boolean>) model);
                    break;
                case LOAD:
                    result = new BooleanViewPanel(id, (IModel<Boolean>) model);
                    break;
                case TRUSTED:
                    result = new BooleanViewPanel(id, (IModel<Boolean>) model);
                    break;
            }
        }
        if (result != null) {
            result.setOutputMarkupId(true);
            componentForUpdate = result;
        }
        return result;
    }

    @Override
    protected IModel<String> newLabelModel() {
        IModel<String> label = Model.of("");
        switch (getPropertyObject()) {
            case GROUP:
                label = new ResourceModel(GROUP);
                break;
            case ARTIFACT:
                label = new ResourceModel(ARTIFACT);
                break;
            case VERSION:
                label = new ResourceModel(VERSION);
                break;
            case REPOSITORY:
                label = new ResourceModel(REPOSITORY);
                break;
            case DESCRIPTION:
                label = new ResourceModel(DESCRIPTION);
                break;
            case LOAD:
                label = new ResourceModel(LOAD);
                break;
            case TRUSTED:
                label = new ResourceModel(TRUSTED);
                break;
            case FILE:
                label = new ResourceModel(FILE);
                break;
        }
        return label;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onAjaxUpdate(AjaxRequestTarget target) {
        OArtifactField criteria = getPropertyObject();
        if (DisplayMode.EDIT.equals(getModeObject()) && componentForUpdate != null && criteria != OArtifactField.FILE) {
            OArtifact oArtifact = getEntityObject();
            switch (criteria) {
                case GROUP:
                    componentForUpdate.setDefaultModelObject(oArtifact.getArtifactReference().getGroupId());
                    break;
                case ARTIFACT:
                    componentForUpdate.setDefaultModelObject(oArtifact.getArtifactReference().getArtifactId());
                    break;
                case VERSION:
                    componentForUpdate.setDefaultModelObject(oArtifact.getArtifactReference().getVersion());
                    break;
                case REPOSITORY:
                    componentForUpdate.setDefaultModelObject(oArtifact.getArtifactReference().getRepository());
                    break;
                case DESCRIPTION:
                    componentForUpdate.setDefaultModelObject(oArtifact.getArtifactReference().getDescription());
                    break;
                case DOWNLOADED:
                    componentForUpdate.setDefaultModelObject(Boolean.valueOf(oArtifact.isDownloaded()));
                    break;
                case LOAD:
                    componentForUpdate.setDefaultModelObject(Boolean.valueOf(oArtifact.isLoad()));
                    break;
                case TRUSTED:
                    componentForUpdate.setDefaultModelObject(Boolean.valueOf(oArtifact.isTrusted()));
                    break;
            }

            target.add(componentForUpdate);
        }
    }

    /**
     * Config jar file upload field if its need
     * @param uploadField - jar file upload field
     */
    protected void configureJarFileUploadField(final FileUploadField uploadField) {

    }
}
