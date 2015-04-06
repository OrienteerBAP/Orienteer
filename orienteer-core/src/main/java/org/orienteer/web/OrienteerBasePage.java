/**
 * Copyright (C) 2015 Ilia Naryzhny (phantom@ydn.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.orienteer.web;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Objects;
import org.orienteer.OrienteerWebSession;
import org.orienteer.components.DefaultPageHeader;
import org.orienteer.components.FAIcon;
import org.orienteer.components.ODocumentPageLink;
import org.orienteer.components.OrienteerFeedbackPanel;
import org.orienteer.modules.PerspectivesModule;
import org.orienteer.web.schema.ListOClassesPage;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;

public abstract class OrienteerBasePage<T> extends BasePage<T> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private OrienteerFeedbackPanel feedbacks;

    public OrienteerBasePage() {
        super();
    }

    public OrienteerBasePage(IModel<T> model) {
        super(model);
    }

    public OrienteerBasePage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    public void initialize() {
        super.initialize();
        add(new BookmarkablePageLink<T>("home", getApplication().getHomePage()));
        add(newPageHeaderComponent("pageHeader"));

        final AttributeAppender highlightActivePerspective = new AttributeAppender("class", "active") {
            @Override
            public boolean isEnabled(Component component) {
                return Objects.isEqual(getPerspective(), component.getDefaultModelObject());
            }
        };

        add(new ListView<ODocument>("perspectives", new OQueryModel<ODocument>("select from " + PerspectivesModule.OCLASS_PERSPECTIVE)) {

            @Override
            protected void populateItem(ListItem<ODocument> item) {
                IModel<ODocument> itemModel = item.getModel();
                Link<ODocument> link = new Link<ODocument>("link", itemModel) {

                    @Override
                    public void onClick() {
                        OrienteerWebSession.get().setPerspecive(getModelObject());
                        OrienteerBasePage.this.info(getLocalizer().getString("info.perspectivechanged", this, new ODocumentPropertyModel<String>(getModel(), "name")));
                    }
                };
                link.add(new FAIcon("icon", new ODocumentPropertyModel<String>(itemModel, "icon")),
                        new Label("name", new ODocumentPropertyModel<String>(itemModel, "name")).setRenderBodyOnly(true));
                item.add(link);
                item.add(highlightActivePerspective);
            }
        });

        boolean signedIn = OrientDbWebSession.get().isSignedIn();
        add(new BookmarkablePageLink<Object>("login", LoginPage.class).setVisible(!signedIn));
        add(new BookmarkablePageLink<Object>("logout", LogoutPage.class).setVisible(signedIn));

        IModel<ODocument> perspectiveModel = new PropertyModel<ODocument>(this, "perspective");
        add(new ListView<ODocument>("perspectiveItems", new ODocumentPropertyModel<List<ODocument>>(perspectiveModel, "menu")) {

            @Override
            protected void populateItem(ListItem<ODocument> item) {
                IModel<ODocument> itemModel = item.getModel();
                ExternalLink link = new ExternalLink("link", new ODocumentPropertyModel<String>(itemModel, "url"));
                link.add(new FAIcon("icon", new ODocumentPropertyModel<String>(itemModel, "icon")),
                        new Label("name", new ODocumentPropertyModel<String>(itemModel, "name")).setRenderBodyOnly(true));
                item.add(link);
            }
        });

        add(feedbacks = new OrienteerFeedbackPanel("feedbacks"));
        add(new ODocumentPageLink("myProfile", new PropertyModel<ODocument>(this, "session.user.document")));

        final IModel<String> queryModel = Model.of();
        Form<String> searchForm = new Form<String>("searchForm", queryModel) {

            @Override
            protected void onSubmit() {
                setResponsePage(new SearchPage(queryModel));
            }

        };
        searchForm.add(new TextField<String>("query", queryModel));
        searchForm.add(new AjaxButton("search") {
        });
        add(searchForm);
    }

    protected Component newPageHeaderComponent(String componentId) {
        return new DefaultPageHeader(componentId, getTitleModel());
    }

    public OrienteerFeedbackPanel getFeedbacks() {
        return feedbacks;
    }

}
