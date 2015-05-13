package org.orienteer.core.widget;

/**
 * {@link IWidgetSettings} for testing
 */
public class RuntimeWidgetSettings implements IWidgetSettings{
	private int col=1;
	private int row=1;
	private int sizeX=1;
	private int sizeY=1;
	
	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getSizeX() {
		return sizeX;
	}

	public void setSizeX(int sizeX) {
		this.sizeX = sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public void setSizeY(int sizeY) {
		this.sizeY = sizeY;
	}

	@Override
	public void persist() {
		// NOP
	}
}
