package org.orienteer.core.component.widget;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.ajax.AjaxRequestHandler;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.command.EditODocumentCommand;
import org.orienteer.core.component.command.EditODocumentsCommand;
import org.orienteer.core.component.command.EditSchemaCommand;
import org.orienteer.core.component.command.SaveOLocalizationsCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.DeleteRowCommandColumn;
import org.orienteer.core.component.table.OPropertyValueColumn;
import org.orienteer.core.component.table.OPropertyValueComboBoxColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.event.ActionPerformedEvent;
import org.orienteer.core.model.LanguagesChoiceProvider;
import org.orienteer.core.module.OrienteerLocalizationModule;
import org.orienteer.core.widget.AbstractModeAwareWidget;

import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for widgets showing and modifying schema localizations.
 *
 *  @param <T> the type of schema object
 */
public abstract class AbstractSchemaLocalizationWidget<T> extends AbstractModeAwareWidget<T> {

    private final AjaxCommand<ODocument> ajaxFormCommand;

    private final Form<T> form;
    private final OrienteerDataTable<ODocument, String> table;

    public AbstractSchemaLocalizationWidget(String id, IModel<T> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
        OClass oLocalizationClass = getDatabase().getMetadata().getSchema().getClass(OrienteerLocalizationModule.OCLASS_LOCALIZATION);

        final OQueryDataProvider<ODocument> provider = new OQueryDataProvider<ODocument>("select from OLocalization where key = :key");
        provider.setParameter("key", Model.of(getLocalizationKey(getModelObject())));

        form = new Form<T>("form");
        List<IColumn<ODocument, String>> columns = new ArrayList<IColumn<ODocument,String>>();
        columns.add(new OPropertyValueColumn(oLocalizationClass.getProperty(OrienteerLocalizationModule.OPROPERTY_VALUE), getModeModel()));
        OProperty langProperty = oLocalizationClass.getProperty(OrienteerLocalizationModule.OPROPERTY_LANG);
        columns.add(new OPropertyValueComboBoxColumn<String>(langProperty, LanguagesChoiceProvider.INSTANCE, getModeModel()));
        columns.add(new DeleteRowCommandColumn(langProperty, form, getModeModel()));

        table = new OrienteerDataTable<ODocument, String>("localizations", columns, provider, 20);
        table.addCommand(new EditODocumentsCommand(table, getModeModel(), new OClassModel(OrienteerLocalizationModule.OCLASS_LOCALIZATION)));
        table.addCommand(new SaveOLocalizationsCommand(table, getModeModel()));
        table.setCaptionModel(new ResourceModel("class.localization"));

        form.add(table);
        add(form);

        ajaxFormCommand = new AjaxCommand<ODocument>("add", "command.add") {
        	{
        		OSecurityHelper.secureComponent(this, OSecurityHelper.requireOClass(OrienteerLocalizationModule.OCLASS_LOCALIZATION, OrientPermission.CREATE));
        	}
            @Override
            public void onClick(AjaxRequestTarget target) {
                ODocument newLocalization = new ODocument(OrienteerLocalizationModule.OCLASS_LOCALIZATION);
                T schemaObject = AbstractSchemaLocalizationWidget.this.getModelObject();
                newLocalization.field(OrienteerLocalizationModule.OPROPERTY_KEY, getLocalizationKey(schemaObject));
                newLocalization.field(OrienteerLocalizationModule.OPROPERTY_LANG, "");
                newLocalization.field(OrienteerLocalizationModule.OPROPERTY_STYLE, "");
                newLocalization.field(OrienteerLocalizationModule.OPROPERTY_VARIATION, "");
                newLocalization.field(OrienteerLocalizationModule.OPROPERTY_VALUE, "");
                newLocalization.field(OrienteerLocalizationModule.OPROPERTY_ACTIVE, false);
                getDatabase().save(newLocalization);
                target.add(table);
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
                    ((ActionPerformedEvent) payload).getTarget().add(ajaxFormCommand);
                } else if (payload instanceof AjaxRequestHandler) {
                    ((AjaxRequestHandler) payload).add(ajaxFormCommand);
                }
            }
        };

        table.addCommand(ajaxFormCommand.setBootstrapType(BootstrapType.PRIMARY)
                .setIcon(FAIconType.language));
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
