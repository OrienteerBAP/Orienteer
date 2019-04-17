package org.orienteer.core.web;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.MountPath;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;

/**
 * Widget based page for display list of {@link ODocument}
 * List of documents parses from page parameters.
 * Parameter 'docs' contains documents rid separated by ','
 */
@MountPath("/documents/list")
public class ODocumentsPage extends AbstractWidgetPage<List<ODocument>> {

    public ODocumentsPage() {
        super();
    }

    public ODocumentsPage(IModel<List<ODocument>> model) {
        super(model);
    }

    public ODocumentsPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected IModel<List<ODocument>> resolveByPageParameters(PageParameters params) {
        String docs = params.get("docs").toOptionalString();
        IModel<List<ODocument>> result = new ListModel<>();

        if (!Strings.isNullOrEmpty(docs)) {
            List<ODocument> docsList = new LinkedList<>();
            result.setObject(docsList);
            if (docs.contains(",")) {
                String [] rids = docs.split(",");
                for (String rid : rids) {
                    docsList.add(new ORecordId(rid).getRecord());
                }
            } else {
                docsList.add(new ORecordId(docs).getRecord());
            }
        }
        return result;
    }

    @Override
    public void initialize() {
        if (getModelObject() == null) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }
        super.initialize();
    }

    @Override
    public IModel<String> getTitleModel() {
        return new StringResourceModel("documents.title");
    }

    @Override
    public String getDomain() {
        return "documents";
    }
}
