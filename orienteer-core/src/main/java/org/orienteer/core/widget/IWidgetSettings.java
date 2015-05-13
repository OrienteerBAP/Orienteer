package org.orienteer.core.widget;

import org.apache.wicket.util.io.IClusterable;

/**
 * Interface for widget settings
 */
public interface IWidgetSettings extends IClusterable {
	public int getCol();
	public void setCol(int col);
	public int getRow();
	public void setRow(int row);
	public int getSizeX();
	public void setSizeX(int sizeX);
	public int getSizeY();
	public void setSizeY(int sizeY);
	public void persist();
}
