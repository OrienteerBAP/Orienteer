package org.orienteer.components;

public enum BootstrapSize
{
	LARGE("large", "btn-lg"),
	DEFAULT("default", ""),
	SMALL("success", "btn-sm"),
	EXTRA_SMALL("info", "btn-xs");
	
	private final String baseCssClass;
	private final String btnCssClass;
	
	private BootstrapSize(String baseCssClass, String btnCssClass)
	{
		this.baseCssClass = baseCssClass;
		this.btnCssClass = btnCssClass;
	}

	public String getBaseCssClass() {
		return baseCssClass;
	}

	public String getBtnCssClass() {
		return btnCssClass;
	}
}
