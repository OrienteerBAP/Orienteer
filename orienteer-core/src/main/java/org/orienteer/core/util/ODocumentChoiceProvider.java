package org.orienteer.core.util;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.service.IOClassIntrospector;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Choice provider for Select2 control
 */
public class ODocumentChoiceProvider extends ChoiceProvider<ODocument> {

    private String className;
    private String propertyName;

    private transient IOClassIntrospector oClassIntrospector;

    public ODocumentChoiceProvider(OClass oClass) {
        this.className =  oClass.getName();
        this.propertyName = getOClassIntrospector().getNameProperty(oClass).getName();
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
        StringBuilder sql = new StringBuilder("SELECT FROM ").append(className);
        if (!Strings.isEmpty(query)) {
            sql.append(" WHERE ")
                    .append(propertyName)
                    .append(" CONTAINSTEXT :query");
        }
        sql.append(" LIMIT 20");
        OQueryModel<ODocument> choicesModel = new OQueryModel<ODocument>(sql.toString());
        choicesModel.setParameter("query", new Model<String>(query));
        response.addAll(choicesModel.getObject());
    }

    @Override
    public Collection<ODocument> toChoices(Collection<String> ids) {
        ArrayList<ODocument> documents = new ArrayList<ODocument>();
        for (String id : ids) {
            ORecordId rid = new ORecordId(id);
            ODocument ret = rid.getRecord();
            documents.add(ret);
        }
        return documents;
    }
}