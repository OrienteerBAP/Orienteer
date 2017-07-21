package org.orienteer.core.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
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

    public ODocumentChoiceProvider(OClass oClass) {
        initOClass(oClass);
    }

    public ODocumentChoiceProvider(IModel<OClass> classModel) {
        this.classModel = classModel;
        if (classModel.getObject() != null)
            initOClass(classModel.getObject());
    }

    private void initOClass(OClass oClass) {
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
        if (classModel != null) {
            response.addAll(queryToOClassModel(query));
        } else if (!Strings.isNullOrEmpty(className)) {
            response.addAll(queryToClassName(query));
        }
    }

    private List<ODocument> queryToOClassModel(String query) {
        List<ODocument> result = Lists.newArrayList();
        if (classModel.getObject() != null || (className != null && propertyName != null)) {
            if (classModel.getObject() != null)
                initOClass(classModel.getObject());
            result = queryToClassName(query);
        }
        return result;
    }

    private List<ODocument> queryToClassName(String query) {
        OQueryModel<ODocument> queryModel = new OQueryModel<>("SELECT FROM " + className);
        IFilterCriteriaManager manager = new FilterCriteriaManager(
                new OPropertyModel(getOClassIntrospector().getNameProperty(classModel.getObject())));
        manager.addFilterCriteria(manager.createContainsStringFilterCriteria(Model.of(query), Model.of(true)));
        queryModel.addFilterCriteriaManager(propertyName, manager);
        return queryModel.getObject();
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