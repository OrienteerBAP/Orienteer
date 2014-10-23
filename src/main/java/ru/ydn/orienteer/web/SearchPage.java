package ru.ydn.orienteer.web;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.annotation.mount.MountPath;

import ru.ydn.orienteer.components.TabsPanel;
import ru.ydn.orienteer.components.commands.CreateODocumentCommand;
import ru.ydn.orienteer.components.commands.DeleteODocumentCommand;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.orienteer.services.IOClassIntrospector;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

@MountPath("/search")
public class SearchPage extends OrienteerBasePage<String>
{
	private static Ordering<OClass> CLASSES_ORDERING = Ordering.natural().nullsFirst().onResultOf(new Function<OClass, String>() {

		@Override
		public String apply(OClass input) {
			return new OClassNamingModel(input).getObject();
		}
	});
	private WebMarkupContainer resultsContainer;
	private IModel<OClass> selectedClassModel;
	
	@Inject
	private IOClassIntrospector oClassIntrospector;
	
	public SearchPage()
	{
		super(Model.of(""));
	}

	public SearchPage(IModel<String> model)
	{
		super(model);
	}

	public SearchPage(PageParameters parameters)
	{
		super(parameters);
	}

	@Override
	protected IModel<String> resolveByPageParameters(
			PageParameters pageParameters) {
		String query = pageParameters.get("q").toOptionalString();
		return Model.of(query);
	}
	
	public List<OClass> getClasses()
	{
		return CLASSES_ORDERING.sortedCopy(getDatabase().getMetadata().getSchema().getClasses());
	}

	@Override
	public void initialize() {
		super.initialize();
		selectedClassModel = new OClassModel(getClasses().get(0));
		
		Form<String> form = new Form<String>("form", getModel());
		form.add(new TextField<String>("query", getModel()));
		form.add(new AjaxButton("search") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				super.onSubmit(target, form);
				target.add(resultsContainer);
			}
		});
		
		form.add(new TabsPanel<OClass>("tabs", selectedClassModel, new PropertyModel<List<OClass>>(this, "classes"))
				{

					@Override
					public void onTabClick(AjaxRequestTarget target) {
						prepareResults();
						target.add(resultsContainer);
					}
			
				});
		
		resultsContainer = new WebMarkupContainer("resultsContainer")
		{
			{
				setOutputMarkupPlaceholderTag(true);
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(!Strings.isEmpty(SearchPage.this.getModelObject()));
			}
			
		};
		
		prepareResults();
		form.add(resultsContainer);
		add(form);
	}
	
	private void prepareResults()
	{
		prepareResults(selectedClassModel.getObject());
	}
	
	private void prepareResults(OClass oClass)
	{
		OQueryDataProvider<ODocument> provider = new OQueryDataProvider<ODocument>("select from "+oClass.getName()+" where any() containstext :text");
		provider.setParameter("text", getModel());
		OrienteerDataTable<ODocument, String> table = new OrienteerDataTable<ODocument, String>("results", oClassIntrospector.getColumnsFor(oClass, false), provider, 20);
		resultsContainer.addOrReplace(table);
	}

	@Override
	public IModel<String> getTitleModel() {
		return new ResourceModel("search.title");
	}
	
	
	
}
