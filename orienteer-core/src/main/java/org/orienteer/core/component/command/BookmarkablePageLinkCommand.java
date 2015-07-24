package org.orienteer.core.component.command;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.table.OrienteerDataTable;

/**
 * Command to render bookmarkable link
 *
 * @param <T> the type of an entity to which this command can be applied
 */
public class BookmarkablePageLinkCommand<T> extends Command<T> {
	
	private final Class<? extends Page> pageClass;
	protected PageParameters parameters;

	public <C extends Page> BookmarkablePageLinkCommand(IModel<?> labelModel,
			OrienteerDataTable<T, ?> table, final Class<C> pageClass) {
		super(labelModel, table);
		this.pageClass = pageClass;
	}

	public <C extends Page> BookmarkablePageLinkCommand(IModel<?> labelModel,
			OrienteerStructureTable<T, ?> table, final Class<C> pageClass) {
		super(labelModel, table);
		this.pageClass = pageClass;
	}

	public <C extends Page> BookmarkablePageLinkCommand(String commandId, String labelKey, final Class<C> pageClass) {
		super(commandId, labelKey);
		this.pageClass = pageClass;
	}

	public <C extends Page> BookmarkablePageLinkCommand(String commandId, String labelKey,
			IModel<T> model, final Class<C> pageClass) {
		super(commandId, labelKey, model);
		this.pageClass = pageClass;
	}

	public <C extends Page> BookmarkablePageLinkCommand(String commandId, IModel<?> labelModel, final Class<C> pageClass) {
		super(commandId, labelModel);
		this.pageClass = pageClass;
	}

	public <C extends Page> BookmarkablePageLinkCommand(String commandId, IModel<?> labelModel,
			IModel<T> model, final Class<C> pageClass) {
		super(commandId, labelModel, model);
		this.pageClass = pageClass;
	}
	
	@Override
	protected AbstractLink newLink(String id) {
		return new BookmarkablePageLink<T>(id, pageClass) {
			@Override
			public PageParameters getPageParameters() {
				return BookmarkablePageLinkCommand.this.getPageParameters();
			}
		};
	}
	
	public PageParameters getPageParameters()
	{
		if (parameters == null)
		{
			parameters = new PageParameters();
		}
		return parameters;
	}
	
	public BookmarkablePageLinkCommand<T> setPageParameters(PageParameters parameters) {
		this.parameters = parameters;
		return this;
	}

	@Override
	public final void onClick() {
		throw new IllegalStateException("OnClick can't be invoked for bookmarkable links");
	}

}
