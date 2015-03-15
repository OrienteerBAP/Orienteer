package org.orienteer.components.table.navigation;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;

public class OrienteerNavigationToolbar extends NavigationToolbar
{
	private static final long serialVersionUID = 1L;

	public OrienteerNavigationToolbar(final DataTable<?, ?> table)
	{
		super(table);
	}

	@Override
	protected PagingNavigator newPagingNavigator(final String navigatorId, final DataTable<?, ?> table)
	{
		return new OrienteerPagingNavigator(navigatorId, table)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onAjaxEvent(final AjaxRequestTarget target)
			{
				target.add(table);
			}
		};
	}
}
