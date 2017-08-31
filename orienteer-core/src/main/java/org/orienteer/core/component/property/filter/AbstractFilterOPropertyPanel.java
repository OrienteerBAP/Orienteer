package org.orienteer.core.component.property.filter;

import com.google.common.collect.Lists;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.orienteer.core.component.BootstrapSize;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxFormCommand;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;

import java.util.List;

/**
 * Filter panel which contains draggable filter container and filter show button.
 */
public abstract class AbstractFilterOPropertyPanel extends Panel {

    private static final String ACTIVE            = "active";
    private static final String TAB_PANE          = "tab-pane";
    private static final String TAB_PANE_ACTIVE   = "tab-pane active";
    private static final String FILTER_WIDTH      = "filter-width";
    private static final String TAB_NAME_TEMPLATE = "widget.document.filter.tab.%s";
    static final String TAB_FILTER_TEMPLATE       = "widget.document.filter.%s";
    public static final String PANEL_ID           = "panel";

    private final String containerId;

    private String currentTabId;

    public AbstractFilterOPropertyPanel(String id, IModel<String> name, final Form form) {
        super(id);
        final List<AbstractFilterPanel> filterPanels = Lists.newArrayList();
        createFilterPanels(filterPanels);
        final WebMarkupContainer container = new WebMarkupContainer("container");
        this.containerId = container.getMarkupId();
        List<FilterTab> tabs = createPanelSwitches("switch", filterPanels);
        addFilterPanels(container, filterPanels, tabs);
        addFilterSwitches(container, tabs);
        container.setOutputMarkupPlaceholderTag(true);
        container.setVisible(false);
        container.add(newOkButton("okButton", container, form));
        container.add(newClearButton("clearButton", container, form, filterPanels));
        add(newShowFilterButton("showFilters", container));
        container.add(new Label("panelTitle", name).setOutputMarkupPlaceholderTag(true));
        add(container);
        setOutputMarkupPlaceholderTag(true);
    }

    protected abstract void createFilterPanels(List<AbstractFilterPanel> filterPanels);

    private AjaxFormCommand<Void> newOkButton(String id, final WebMarkupContainer container, final Form form) {
        return new AjaxFormCommand<Void>(id, Model.of("OK")) {
            @Override
            protected AbstractLink newLink(String id) {
                return new AjaxSubmitLink(id, form) {

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        container.setVisible(false);
                        target.add(container);
                        target.appendJavaScript(removeFilterJs(containerId));
                    }
                };
            }

            @Override
            protected void onInstantiation() {
                super.onInstantiation();
                setBootstrapType(BootstrapType.PRIMARY);
                setBootstrapSize(BootstrapSize.SMALL);
                setIcon(FAIconType.check);
            }
        };
    }

    private AjaxFormCommand<Void> newClearButton(String id, final WebMarkupContainer container, final Form form,
                                                 final List<AbstractFilterPanel> filterPanels) {
        return new AjaxFormCommand<Void>(id, "widget.document.filter.clear") {
            @Override
            protected AbstractLink newLink(String id) {
                return new AjaxSubmitLink(id, form) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        for (AbstractFilterPanel panel : filterPanels) {
                            panel.clearInputs(target);
                        }
                        target.add(container);
                    }
                };
            }

            @Override
            protected void onInstantiation() {
                super.onInstantiation();
                setBootstrapSize(BootstrapSize.SMALL);
                setBootstrapType(BootstrapType.DANGER);
                setIcon(FAIconType.trash);
            }
        };
    }

    private AjaxFallbackLink<Void> newShowFilterButton(String id, final WebMarkupContainer container) {
        return new AjaxFallbackLink<Void>(id) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                boolean visible = !container.isVisible();
                container.setVisible(visible);
                target.add(container);
                if (visible) {
                    target.appendJavaScript(initFilterJs(containerId, currentTabId));
                } else target.appendJavaScript(removeFilterJs(containerId));
            }
        };
    }

    private void addFilterPanels(WebMarkupContainer container, List<AbstractFilterPanel> panels, final List<FilterTab> tabs) {
        ListView<AbstractFilterPanel> listView = new ListView<AbstractFilterPanel>("filterPanels", panels) {
            private boolean first = true;
            @Override
            protected void populateItem(ListItem<AbstractFilterPanel> item) {
                if (first) {
                    first = false;
                    item.add(AttributeModifier.append("class", TAB_PANE_ACTIVE));
                } else item.add(AttributeModifier.append("class", TAB_PANE));
                item.add(AttributeModifier.append("class", FILTER_WIDTH));
                AbstractFilterPanel panel = item.getModelObject();
                for (FilterTab tab : tabs) {
                    if (tab.getType().equals(panel.getFilterCriteriaType())) {
                        tab.setTabId(item.getMarkupId());
                        break;
                    }
                }
                item.add(panel);
            }
        };
        listView.setOutputMarkupPlaceholderTag(true);
        listView.setReuseItems(true);
        container.add(listView);
    }

    private void addFilterSwitches(WebMarkupContainer container, List<FilterTab> switches) {
        ListView<FilterTab> listView = new ListView<FilterTab>("panelSwitches", switches) {

            private boolean first = true;

            @Override
            protected void populateItem(ListItem<FilterTab> item) {
                if (first) {
                    first = false;
                    item.add(AttributeModifier.append("class", ACTIVE));
                }
                item.add(item.getModelObject());
            }
        };
        listView.setOutputMarkupPlaceholderTag(true);
        listView.setReuseItems(true);
        container.add(listView);
    }

    private List<FilterTab> createPanelSwitches(String id, List<AbstractFilterPanel> panels) {
        final List<FilterTab> switches = Lists.newArrayList();

        for (AbstractFilterPanel panel : panels) {
            if (panel == null)
                continue;
            final FilterCriteriaType type = panel.getFilterCriteriaType();
            FilterTab tab = new FilterTab(id, type);
            tab.setBody(new ResourceModel(String.format(TAB_NAME_TEMPLATE, type.getName())));
            switches.add(tab);

        }
        return switches;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(
                new JavaScriptResourceReference(AbstractFilterOPropertyPanel.class, "filter.js")));
        response.render(CssHeaderItem.forReference(
                new CssResourceReference(AbstractFilterOPropertyPanel.class, "filter.css")));
        response.render(OnDomReadyHeaderItem.forScript(initFilterJs(containerId, currentTabId)));
    }

    private String initFilterJs(String containerId, String tabId) {
        return tabId != null ? String.format("; initFilter('%s', '%s');", containerId, tabId) :
                String.format("; initFilter('%s');", containerId);
    }

    private String showTabJs(String containerId, String id) {
        return String.format("; switchFilterTab('%s', '%s')", containerId, id);
    }

    private String removeFilterJs(String id) {
        return String.format("; removeFilter('%s');", id);
    }

    /**
     * Switch for filter panels
     */
    private class FilterTab extends AjaxFallbackLink<Void> {

        private final FilterCriteriaType type;
        private String tabId;

        public FilterTab(String id, FilterCriteriaType type) {
            super(id);
            this.type = type;
            setOutputMarkupId(true);
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            currentTabId = getMarkupId();
            target.appendJavaScript(showTabJs(containerId, currentTabId));
        }

        public FilterCriteriaType getType() {
            return type;
        }

        public void setTabId(String tabId) {
            this.tabId = tabId;
        }

        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            tag.remove("href");
            tag.put("href", "#" + tabId);
        }
    }
}
