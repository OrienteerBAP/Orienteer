package org.orienteer.core.component.structuretable;

import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * {@link Panel} to allow add toolbars to a {@link StructureTable}
 *
 * @param <P> the type of main object for a table
 */
public class AbstractStructureTableToolbar<P> extends GenericPanel<P>
{
	private static final long serialVersionUID = 1L;

	private final StructureTable<P, ?> table;

	/**
	 * Constructor
	 * 
	 * @param model
	 *            model
	 * @param table
	 *            data table this toolbar will be attached to
	 */
	public AbstractStructureTableToolbar(final IModel<P> model, final StructureTable<P, ?> table)
	{
		super(table.newToolbarId(), model);
		this.table = table;
	}

	/**
	 * Constructor
	 * 
	 * @param table
	 *            data table this toolbar will be attached to
	 */
	public AbstractStructureTableToolbar(final StructureTable<P, ?> table)
	{
		this(table.getModel(), table);
	}

	/**
	 * @return DataTable this toolbar is attached to
	 */
	public StructureTable<P, ?> getTable()
	{
		return table;
	}
}
