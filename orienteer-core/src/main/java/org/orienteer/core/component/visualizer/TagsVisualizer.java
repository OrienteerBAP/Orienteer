package org.orienteer.core.component.visualizer;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.orientechnologies.orient.core.id.ORecordId;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.util.ODocumentChoiceRenderer;

import org.wicketstuff.select2.*;
import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.utils.OChoiceRenderer;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link IVisualizer} to show links as tags control
 */
public class TagsVisualizer extends AbstractSimpleVisualizer {
    public TagsVisualizer() {
        super("tags", false, OType.LINK, OType.LINKLIST, OType.LINKSET, OType.LINKBAG);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Component createComponent(String id, DisplayMode mode,
                                         IModel<ODocument> documentModel, IModel<OProperty> propertyModel, IModel<V> valueModel) {
        if (DisplayMode.EDIT.equals(mode)) {
            OProperty property = propertyModel.getObject();
            if (property.getType().isMultiValue()) {
                return new Select2MultiChoice<ODocument>(id, (IModel<Collection<ODocument>>) valueModel, new ODocumentChoiceProvider(propertyModel));
            } else {
                return new Select2Choice<ODocument>(id, (IModel<ODocument>) valueModel, new ODocumentChoiceProvider(propertyModel));
            }
        } else {
            return null;
        }
    }

    public class ODocumentChoiceProvider extends ChoiceProvider<ODocument> {

        private transient IOClassIntrospector oClassIntrospector;

        public IModel<OProperty> propertyModel;

        public ODocumentChoiceProvider(IModel<OProperty> propertyModel) {
            this.propertyModel = propertyModel;
        }

        protected IOClassIntrospector getOClassIntrospector() {
            if (oClassIntrospector == null) {
                oClassIntrospector = OrienteerWebApplication.get().getOClassIntrospector();
            }
            return oClassIntrospector;
        }

        @Override
        public String getDisplayValue(ODocument document) {
            return getOClassIntrospector().getDocumentName(document);
        }

        @Override
        public String getIdValue(ODocument document) {
            return document.getIdentity().toString();
        }

        @Override
        public void query(String query, int i, Response<ODocument> response) {
            OProperty property = propertyModel.getObject();
            OClass oClass = property.getLinkedClass();
            StringBuilder sql = new StringBuilder("SELECT FROM " + oClass.getName());
            if (!Strings.isEmpty(query)) {
                sql.append(" WHERE " + getOClassIntrospector().getNameProperty(oClass).getName() + " LIKE '%" + query + "%'");
            }
            sql.append(" LIMIT 100");
            OQueryModel<ODocument> choicesModel = new OQueryModel<ODocument>(sql.toString());
            response.addAll(choicesModel.getObject());
        }

        @Override
        public Collection<ODocument> toChoices(Collection<String> ids) {
            ArrayList<ODocument> documents = new ArrayList<ODocument>();
            for (String id : ids) {
                ORecordId rid = new ORecordId(id);
                ODocument ret =  rid.getRecord();
                documents.add(ret);
            }
            return documents;
        }
    }

}