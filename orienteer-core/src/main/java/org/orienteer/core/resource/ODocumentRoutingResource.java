package org.orienteer.core.resource;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.orienteer.core.MountPath;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.RoutingModule;
import org.orienteer.core.web.ODocumentPage;
import org.orienteer.core.web.ODocumentsPage;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Resource which resolved document routing.
 * See {@link RoutingModule}
 */
@MountPath("/address/${address}")
public class ODocumentRoutingResource extends AbstractResource {

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
        ResourceResponse response = new ResourceResponse();
        if (response.dataNeedsToBeWritten(attributes)) {
            String address = attributes.getParameters().get("address").toOptionalString();
            response.setWriteCallback(createWriteCallback(address));
        }
        return response;
    }

    private WriteCallback createWriteCallback(String address) {
        return new WriteCallback() {
            @Override
            public void writeData(Attributes attributes) throws IOException {
                List<ODocument> documents = getAddressDocuments();
                if (documents.size() == 1) {
                    redirectToDocumentPage(documents.get(0));
                } else if (!documents.isEmpty()) {
                    redirectToDocumentsPage(documents);
                } else {
                    redirectToNotFoundPage();
                }
            }

            private List<ODocument> getAddressDocuments() {
                return DBClosure.sudo(db -> {
                    RoutingModule routing = (RoutingModule) OrienteerWebApplication.lookupApplication().getModuleByName(RoutingModule.NAME);
                    return routing.getRouterNodeDocuments(db, address.startsWith("/") ? address : "/" + address);
                });
            }
        };
    }

    private void redirectToDocumentPage(ODocument document) throws RestartResponseException {
        PageParameters params = new PageParameters();
        params.add("rid", document.getIdentity().toString().substring(1));
        silentRedirect(ODocumentPage.class, params);
    }

    private void redirectToDocumentsPage(List<ODocument> documents) throws RestartResponseException {
        String docsParams = documents.stream().map(ODocument::getIdentity)
                .map(OIdentifiable::getIdentity)
                .map(ORID::toString)
                .map(rid -> rid.substring(1))
                .collect(Collectors.joining(","));


        PageParameters params = new PageParameters();
        params.add("docs", docsParams);
        silentRedirect(ODocumentsPage.class, params);
    }

    private void redirectToNotFoundPage() throws AbortWithHttpErrorCodeException {
        throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
    }

    private void silentRedirect(Class<? extends Page> page, PageParameters params) throws RestartResponseException {

        PageProvider provider = new PageProvider(page, params);
        throw new RestartResponseException(provider, RenderPageRequestHandler.RedirectPolicy.NEVER_REDIRECT);
    }
}
