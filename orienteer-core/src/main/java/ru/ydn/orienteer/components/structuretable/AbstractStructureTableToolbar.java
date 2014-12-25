package ru.ydn.orienteer.components.structuretable;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class AbstractStructureTableToolbar<P> extends Panel
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
	public AbstractStructureTableToolbar(final IModel<?> model, final StructureTable<P, ?> table)
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
		this(null, table);
	}

	/**
	 * @return DataTable this toolbar is attached to
	 */
	public StructureTable<P, ?> getTable()
	{
		return table;
	}
}
