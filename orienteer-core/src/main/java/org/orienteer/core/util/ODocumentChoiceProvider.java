package org.orienteer.core.util;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.service.IOClassIntrospector;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;

import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 
 * Choice provider for Select2 control
 *
 * @param <M> type of main object for ChoiceProvider: should be subtype of {@link OIdentifiable}
 */
public class ODocumentChoiceProvider<M extends OIdentifiable> extends ChoiceProvider<M> {

	private static final long serialVersionUID = 1L;
    
    private IModel<OClass> classModel;

    private transient IOClassIntrospector oClassIntrospector;
    
    public ODocumentChoiceProvider(IModel<OClass> classModel) {
    	this.classModel = classModel;
    }

    public ODocumentChoiceProvider(OClass oClass) {
    	this(new OClassModel(oClass));
    }

    protected IOClassIntrospector getOClassIntrospector() {
        if (oClassIntrospector == null) {
            oClassIntrospector = OrienteerWebApplication.get().getOClassIntrospector();
        }
        return oClassIntrospector;
    }

    @Override
    public String getDisplayValue(M document) {
        return getOClassIntrospector().getDocumentName(document.getRecord());
    }

    @Override
    public String getIdValue(M document) {
        return document.getIdentity().toString();
    }

    @Override
    public void query(String query, int i, Response<M> response) {
    	OClass oClass = classModel.getObject();
    	if(oClass==null) return;
    	OProperty property = getOClassIntrospector().getNameProperty(oClass);
    	if(property==null) return;
        StringBuilder sql = new StringBuilder("SELECT FROM ").append(oClass.getName());
        if (!Strings.isEmpty(query)) {
            sql.append(" WHERE ")
                    .append(property.getName());
            if(!OType.STRING.equals(property.getType())) sql.append(".asString()");
            sql.append(" CONTAINSTEXT :query");
        }
        sql.append(" LIMIT 20");
        OQueryModel<ODocument> choicesModel = new OQueryModel<ODocument>(sql.toString());
        choicesModel.setParameter("query", new Model<String>(query));
        response.addAll((List<M>)choicesModel.getObject());
    }

    @Override
    public Collection<M> toChoices(Collection<String> ids) {
        ArrayList<M> documents = new ArrayList<M>();
        for (String id : ids) {
            ORecordId rid = new ORecordId(id);
            documents.add(rid.getRecord());
        }
        return documents;
    }
    
    @Override
    public void detach() {
    	super.detach();
    	if(classModel!=null) classModel.detach();
    }
}
