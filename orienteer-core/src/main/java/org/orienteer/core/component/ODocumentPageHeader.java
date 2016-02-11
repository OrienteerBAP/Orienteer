package org.orienteer.core.component;

import java.util.List;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.lang.Objects;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;
import org.orienteer.core.service.IOClassIntrospector;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Page header that has been used for a {@link ODocument} related pages.
 * Header shows "navigation path" till this {@link ODocument}
 */
public class ODocumentPageHeader extends GenericPanel<ODocument>
{
	@Inject
	private IOClassIntrospector inspector;
	
	private class GetNavigationPathModel extends LoadableDetachableModel<List<ODocument>>
	{

		@Override
		protected List<ODocument> load() {
			return inspector.getNavigationPath(ODocumentPageHeader.this.getModelObject(), true);
		}
		
	}

	public ODocumentPageHeader(String id, IModel<ODocument> model)
	{
		super(id, model);
		add(new ListView<ODocument>("child", new GetNavigationPathModel()) {

			@Override
			protected void populateItem(ListItem<ODocument> item) {
				item.add(new ODocumentPageLink("link", item.getModel())
						{
							@Override
							protected void onComponentTag(org.apache.wicket.markup.ComponentTag tag) {
								super.onComponentTag(tag);
								if(!isEnabledInHierarchy()) {
									tag.setName("span");
									tag.remove("href");
								}
							};
						}.setDocumentNameAsBody(true));
			}
			
			@Override
			protected ListItem<ODocument> newItem(int index,
					IModel<ODocument> itemModel) {
				return new ListItem<ODocument>(index, itemModel){
					@Override
					protected void onComponentTag(ComponentTag tag) {
						super.onComponentTag(tag);
						if(!isEnabledInHierarchy()) {
							tag.append("class", "active", " ");
						}
					}
					public boolean isEnabledInHierarchy() {
						return !Objects.isEqual(getModelObject(), ODocumentPageHeader.this.getModelObject());
					}
				};
			}
		});
		add(UpdateOnActionPerformedEventBehavior.INSTANCE_CHANGING_CONTINUE);
	}

}
