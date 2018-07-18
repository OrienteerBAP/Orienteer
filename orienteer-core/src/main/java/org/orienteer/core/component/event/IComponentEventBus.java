package org.orienteer.core.component.event;

import com.google.inject.ImplementedBy;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.function.Consumer;

/**
 * Interface for component event bus
 */
@ImplementedBy(DefaultComponentEventBusImpl.class)
public interface IComponentEventBus {

    /**
     * Add listener to event bus
     * @param event {@link String} event name
     * @param listener {@link Consumer<AjaxRequestTarget>} listener which will be calls when even fires
     */
    public void addListener(String event, Consumer<AjaxRequestTarget> listener);

    /**
     * Calls all event listeners for event
     * @param event {@link String} event name
     * @param target {@link AjaxRequestTarget} target which will be used as listener argument
     */
    public void onEvent(String event, AjaxRequestTarget target);

    /**
     * Remove listener from bus by event name
     * @param event {@link String} event name
     * @param listener {@link Consumer<AjaxRequestTarget>} listener for remove
     * @return true if lister was removed
     */
    public boolean removeListener(String event, Consumer<AjaxRequestTarget> listener);

    /**
     * Remove listener from bus. First search collection which contains listener an then remove listener from it.
     * @param listener {@link Consumer<AjaxRequestTarget>} listener for remove
     * @return true if listener was removed
     */
    public boolean removeListener(Consumer<AjaxRequestTarget> listener);

    /**
     * Remove all listeners from bus for event
     * @param event {@link String} event name
     * @return true if listeners was removed
     */
    public boolean removeListeners(String event);
}
