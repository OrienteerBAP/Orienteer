package org.orienteer.core.util;

import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.service.IOClassIntrospector;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaManager;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Choice provider for Select2 control
 */
public class ODocumentChoiceProvider extends ChoiceProvider<ODocument> {

    private String className;
    private String propertyName;
    
    private IModel<OClass> classModel;

    private transient IOClassIntrospector oClassIntrospector;
    
    public ODocumentChoiceProvider(IModel<OClass> classModel) {
    	this.classModel = classModel;
    }

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
    	String className = this.className;
    	String propertyName = this.propertyName;
    	if(className==null && classModel!=null) {
    		OClass oClass = classModel.getObject();
    		if(oClass!=null) {
    			className = oClass.getName();
    			propertyName = getOClassIntrospector().getNameProperty(oClass).getName(); 
    		}
    	}
    	if(className==null || propertyName==null) return;
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
