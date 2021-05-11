package org.orienteer.core.component;

/**
 * {@link Enum} for specification of bootstrap type: default, primary, success and etc.
 */
public enum BootstrapType
{
	PRIMARY("primary", "btn-primary"),
	SECONDARY("secondary", "btn-secondary"),
	SUCCESS("success", "btn-success"),
	INFO("info", "btn-info"),
	WARNING("warning", "btn-warning"),
	DANGER("danger", "btn-danger"),
	LIGHT("light", "btn-light"),
	DARK("dark", "btn-dark"),
	LINL("link", "btn-link");
	
	private final String baseCssClass;
	private final String btnCssClass;
	
	private BootstrapType(String baseCssClass, String btnCssClass)
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
