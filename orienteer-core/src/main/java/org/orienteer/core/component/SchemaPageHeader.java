package org.orienteer.core.component;

import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;
import org.orienteer.core.component.property.AbstractLinkViewPanel;
import org.orienteer.core.component.property.OClassViewPanel;
import org.orienteer.core.web.schema.SchemaPage;

import ru.ydn.wicket.wicketorientdb.model.OClassModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Page header for schema specific entities: {@link OClass}, {@link OProperty}, {@link OIndex}
 */
public class SchemaPageHeader extends GenericPanel<OClass> {
	private RepeatingView childRepeatingView;
	
	private IModel<List<OClass>> classPathModel = new LoadableDetachableModel<List<OClass>>() {

		@Override
		protected List<OClass> load() {
			OClass currentClass = SchemaPageHeader.this.getModelObject();
			List<OClass> superClasses;
			List<OClass> breadCrumbs = new ArrayList<OClass>();
			if (currentClass != null) {
				while ((superClasses = currentClass.getSuperClasses()) != null && !superClasses.isEmpty()) {
					currentClass = superClasses.get(0);
					breadCrumbs.add(currentClass);
				}
				breadCrumbs = Lists.reverse(breadCrumbs);
			}
			return breadCrumbs;
		}
	};
	
	public SchemaPageHeader(String id) {
		this(id, null);
	}

	public SchemaPageHeader(String id, IModel<OClass> oClassModel) {
		super(id, oClassModel);
		add(new BookmarkablePageLink<Object>("schema", SchemaPage.class)
						.setBody(new ResourceModel("menu.list.schema")));
		
		add(new ListView<OClass>("classes", classPathModel) {

			@Override
			protected void populateItem(ListItem<OClass> item) {
				item.add(new OClassPageLink("link", item.getModel()).setClassNameAsBody(false));
			}
		});
		childRepeatingView = new RepeatingView("child");
		add(childRepeatingView);
		add(UpdateOnActionPerformedEventBehavior.INSTANCE_CHANGING_CONTINUE);
	}
	
	public SchemaPageHeader addChild(Component component)
	{
		childRepeatingView.add(component);
		return this;
	}
	
	public String newChildId()
	{
		return childRepeatingView.newChildId();
	}
	
}
