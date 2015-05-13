package org.orienteer.core.widget;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * Descriptor for widgets. 
 */
public class OWidgetDescriptor extends ODocumentWrapper implements IWidgetSettings {
	public static final String OCLASS_WIDGET = "OWidget";
	public static final String OPROPERTY_DASHBOARD = "dashboard";
	public static final String OPROPERTY_TYPE_ID = "typeId";
	public static final String OPROPERTY_COL = "col";
	public static final String OPROPERTY_ROW = "row";
	public static final String OPROPERTY_SIZE_X = "sizeX";
	public static final String OPROPERTY_SIZE_Y = "sizeY";
	
	public OWidgetDescriptor(ODocument iDocument) {
		super(iDocument);
	}
	public OWidgetDescriptor(ORID iRID) {
		super(iRID);
	}
	public OWidgetDescriptor() {
		super(OCLASS_WIDGET);
	}
	
	public ODashboardDescriptor getDashboard()
	{
		return new ODashboardDescriptor((ORID)document.field(OPROPERTY_DASHBOARD, ORID.class));
	}
	
	public OWidgetDescriptor setDashboard(ODashboardDescriptor dashboard) {
		document.field(OPROPERTY_DASHBOARD, dashboard.getDocument());
		return this;
	}
	
	public String getTypeId() {
		return document.field(OPROPERTY_TYPE_ID);
	}
	
	public OWidgetDescriptor setTypeId(String typeId) {
		document.field(OPROPERTY_TYPE_ID, typeId);
		return this;
	}
	
	public int getCol() {
		Integer ret =  document.field(OPROPERTY_COL);
		return ret!=null?ret:1;
	}
	
	public void setCol(int col) {
		document.field(OPROPERTY_COL, col);
	}
	
	public int getRow() {
		Integer ret =  document.field(OPROPERTY_ROW);
		return ret!=null?ret:1;
	}
	
	public void setRow(int row) {
		document.field(OPROPERTY_ROW, row);
	}
	
	public int getSizeX() {
		Integer ret = document.field(OPROPERTY_SIZE_X);
		return ret!=null?ret:2;
	}
	
	public void setSizeX(int sizeX) {
		document.field(OPROPERTY_SIZE_X, sizeX);
	}
	
	public int getSizeY() {
		Integer ret = document.field(OPROPERTY_SIZE_Y);
		return ret!=null?ret:1;
	}
	
	public void setSizeY(int sizeY) {
		document.field(OPROPERTY_SIZE_Y, sizeY);
	}
	@Override
	public void persist() {
		save();
	}
	
}
