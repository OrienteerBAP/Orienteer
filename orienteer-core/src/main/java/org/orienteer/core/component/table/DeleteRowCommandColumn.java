package org.orienteer.core.component.table;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.BootstrapSize;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.command.AjaxFormCommand;
import org.orienteer.core.component.meta.AbstractMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;

/**
 * {@link OrienteerDataTable} Column with 'delete' button.
 */
public class DeleteRowCommandColumn extends AbstractModeMetaColumn<ODocument, DisplayMode, OProperty, String> {

    private Component parent;

    public DeleteRowCommandColumn(OProperty property, Component parent, IModel<DisplayMode> modeModel) {
        super(new OPropertyModel(property), modeModel);
        this.parent = parent;
    }

    @Override
    protected <V> AbstractMetaPanel<ODocument, OProperty, V> newMetaPanel(String componentId, IModel<OProperty> criteryModel, final IModel<ODocument> rowModel) {
        return new AbstractMetaPanel<ODocument, OProperty, V>(componentId, rowModel, criteryModel) {
            @Override
            protected IModel<String> newLabelModel() {
                return new OPropertyNamingModel(getCriteryModel());
            }

            @Override
            protected Component resolveComponent(String id, OProperty critery) {
                    return new AjaxFormCommand(id, "command.remove") {

                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            super.onClick(target);
                            rowModel.getObject().delete();
                            target.add(parent);
                        }

                        @Override
                        protected void onConfigure() {
                            super.onConfigure();
                            setVisibilityAllowed(getModeModel().getObject().equals(DisplayMode.EDIT));
                        }

                    }.setBootstrapSize(BootstrapSize.EXTRA_SMALL)
                            .setBootstrapType(BootstrapType.DANGER)
                            .setIcon((String) null);
            }

            @Override
            protected IModel<V> resolveValueModel() {
                return new DynamicPropertyValueModel<V>(getEntityModel(), getPropertyModel());
            }
        };
    }

    @Override
    protected IModel<String> newLabelModel() {
        return new Model<String>("");
    }
}
