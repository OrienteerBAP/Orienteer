package org.orienteer.core.component.widget;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.ajax.AjaxRequestHandler;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.command.EditODocumentsCommand;
import org.orienteer.core.component.command.SaveOLocalizationsCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.DeleteRowCommandColumn;
import org.orienteer.core.component.table.OPropertyValueColumn;
import org.orienteer.core.component.table.OPropertyValueComboBoxColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.component.GenericTablePanel;
import org.orienteer.core.dao.DAO;
import org.orienteer.core.event.ActionPerformedEvent;
import org.orienteer.core.model.LanguagesChoiceProvider;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.orienteer.core.module.OrienteerLocalizationModule.IOLocalization;

/**
 * Base class for widgets showing and modifying schema localizations.
 *
 *  @param <T> the type of schema object
 */
public abstract class AbstractSchemaLocalizationWidget<T> extends AbstractModeAwareWidget<T> {

    private final AjaxCommand<ODocument> ajaxFormCommand;
    private final OrienteerDataTable<ODocument, String> table;

    public AbstractSchemaLocalizationWidget(String id, IModel<T> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
        OClass oLocalizationClass = getSchema().getClass(IOLocalization.CLASS_NAME);

        final OQueryDataProvider<ODocument> provider = new OQueryDataProvider<ODocument>("select from OLocalization where key = :key");
        provider.setParameter("key", Model.of(getLocalizationKey(getModelObject())));


        List<IColumn<ODocument, String>> columns = new ArrayList<IColumn<ODocument,String>>();
        columns.add(new OPropertyValueColumn(oLocalizationClass.getProperty("value"), getModeModel()));
        OProperty langProperty = oLocalizationClass.getProperty("language");
        columns.add(new OPropertyValueComboBoxColumn<>(langProperty, LanguagesChoiceProvider.INSTANCE, getModeModel()));
        columns.add(new DeleteRowCommandColumn(getModeModel()));
        GenericTablePanel<ODocument> tablePanel = new GenericTablePanel<ODocument>("localizations", columns, provider, 20);
        table = tablePanel.getDataTable();

        table.addCommand(new EditODocumentsCommand(table, getModeModel(), new OClassModel(oLocalizationClass)));
        table.addCommand(new SaveOLocalizationsCommand(table, getModeModel()));
        table.setCaptionModel(new ResourceModel("class.localization"));

        ajaxFormCommand = new AjaxCommand<ODocument>("add", "command.add") {
        	{
        		OSecurityHelper.secureComponent(this, OSecurityHelper.requireOClass(IOLocalization.CLASS_NAME, OrientPermission.CREATE));
        	}
            @Override
            public void onClick(Optional<AjaxRequestTarget> targetOptional) {
            	T schemaObject = AbstractSchemaLocalizationWidget.this.getModelObject();
            	DAO.create(IOLocalization.class)
            			.setKey(getLocalizationKey(schemaObject))
            			.save();
                targetOptional.ifPresent(target -> target.add(table));
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(getModeModel().getObject().equals(DisplayMode.EDIT));
            }

            @Override
            public void onEvent(IEvent<?> event) {
                super.onEvent(event);

                Object payload = event.getPayload();
                if(payload instanceof ActionPerformedEvent) {
                    ajaxFormCommand.setVisibilityAllowed(getModeModel().getObject().equals(DisplayMode.EDIT));
                    ((ActionPerformedEvent<?>) payload).getTarget().ifPresent(target -> target.add(ajaxFormCommand));
                } else if (payload instanceof AjaxRequestHandler) {
                    ((AjaxRequestHandler) payload).add(ajaxFormCommand);
                }
            }
        };

        table.addCommand(ajaxFormCommand.setBootstrapType(BootstrapType.PRIMARY)
                .setIcon(FAIconType.language));
        add(tablePanel);
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.language);
    }

    protected abstract String getLocalizationKey(T oProperty);

    @Override
    protected String getWidgetStyleClass() {
        return "strict";
    }
}
