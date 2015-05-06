package org.orienteer.core.component;

/**
 * {@link Enum} for specification of bootstrap type: default, primary, success and etc.
 */
public enum BootstrapType
{
	DEFAULT("default", "btn-default"),
	PRIMARY("primary", "btn-primary"),
	SUCCESS("success", "btn-success"),
	INFO("info", "btn-info"),
	WARNING("warning", "btn-warning"),
	DANGER("danger", "btn-danger");
	
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
