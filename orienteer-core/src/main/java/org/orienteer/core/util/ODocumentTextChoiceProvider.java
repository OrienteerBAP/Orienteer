package org.orienteer.core.util;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
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
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Provider for Select2MultiChoice which provides {@link Collection<ODocument>}.
 * Depends of {@link Collection<String>} of {@link OClass} names
 */
public class ODocumentTextChoiceProvider extends ChoiceProvider<ODocument> {

    private final IModel<Collection<String>> classNamesModel;
    private transient IOClassIntrospector oClassIntrospector;

    /**
     * Constructor
     * @param classNamesModel {@link IModel<Collection<String>>} of class names which contains provides documents
     */
    public ODocumentTextChoiceProvider(IModel<Collection<String>> classNamesModel) {
        this.classNamesModel = classNamesModel;
    }

    protected IOClassIntrospector getOClassIntrospector() {
        if (oClassIntrospector == null) {
            oClassIntrospector = OrienteerWebApplication.get().getOClassIntrospector();
        }
        return oClassIntrospector;
    }

    @Override
    public void query(String term, int page, Response<ODocument> response) {
        OSchema schema = OrienteerWebApplication.lookupApplication().getDatabase().getMetadata().getSchema();
        if (classNamesModel.getObject() != null && !classNamesModel.getObject().isEmpty()) {
            for (String className : classNamesModel.getObject()) {
                OClass oClass = schema.getClass(className);
                if (oClass != null) {
                    OQueryModel<ODocument> queryModel = new OQueryModel<>("SELECT FROM " + className);
                    String nameProperty = getOClassIntrospector().getNameProperty(oClass).getName();
                    IFilterCriteriaManager manager = new FilterCriteriaManager(
                            new OPropertyModel(getOClassIntrospector().getNameProperty(oClass)));
                    manager.setFilterCriteria(FilterCriteriaType.CONTAINS_TEXT,
                            manager.createContainsStringFilterCriteria(Model.of(term), Model.of(true)));
                    queryModel.addFilterCriteriaManager(nameProperty, manager);
                    response.addAll(queryModel.getObject());
                }
            }
        }
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
    public Collection<ODocument> toChoices(final Collection<String> ids) {
        List<ODocument> documents = new ArrayList<ODocument>();
        for (String id : ids) {
            ORecordId rid = new ORecordId(id);
            ODocument ret = rid.getRecord();
            documents.add(ret);
        }
        return documents;
    }
}
