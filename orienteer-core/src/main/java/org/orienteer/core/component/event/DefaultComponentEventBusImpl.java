package org.orienteer.core.component.event;

import com.google.inject.Singleton;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Default implementation for {@link IComponentEventBus}
 */
@Singleton
public class DefaultComponentEventBusImpl implements IComponentEventBus {

    private final Map<String, List<Consumer<AjaxRequestTarget>>> listeners = new ConcurrentHashMap<>();

    @Override
    public void addListener(String event, Consumer<AjaxRequestTarget> listener) {
        listeners.compute(event, (key, events) -> {
            if (events == null) events = new LinkedList<>();
            events.add(listener);
            return events;
        });
    }

    @Override
    public void onEvent(String event, AjaxRequestTarget target) {
        List<Consumer<AjaxRequestTarget>> consumers = listeners.get(event);
        if (consumers != null) {
            consumers.forEach(c -> c.accept(target));
        }
    }

    @Override
    public boolean removeListener(String event, Consumer<AjaxRequestTarget> listener) {
        List<Consumer<AjaxRequestTarget>> consumers = listeners.get(event);
        return consumers != null && consumers.remove(listener);
    }

    @Override
    public boolean removeListener(Consumer<AjaxRequestTarget> listener) {
        for (List<Consumer<AjaxRequestTarget>> list : listeners.values()) {
            if (list.remove(listener)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeListeners(String event) {
        return listeners.remove(event) != null;
    }

}
