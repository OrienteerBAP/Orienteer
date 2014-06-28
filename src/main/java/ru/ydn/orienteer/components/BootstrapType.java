package ru.ydn.orienteer.components;

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
