package org.orienteer.core.component.property;

import com.google.inject.Inject;
import org.apache.wicket.Component;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.core.service.IMarkupProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link FormComponentPanel} to edit parameters of a function
 *
 * @param <V> the type of collection's objects
 */
public class FunctionParametersMapPanel<V> extends FormComponentPanel<Map<String, V>> {

    private ListView<Pair<V>> listView;

    private List<Pair<V>> data;
    @Inject
    private IMarkupProvider markupProvider;

    public FunctionParametersMapPanel(String id, IModel<Map<String, V>> model) {
        super(id, model);

        listView = new ListView<Pair<V>>("items", new PropertyModel<List<Pair<V>>>(this, "data")) {

            @Override
            protected void populateItem(final ListItem<Pair<V>> item) {
                item.add(new Label("itemLabel", new PropertyModel<V>(item.getModel(), "key")));
                item.add(new TextField<V>("item", new PropertyModel<V>(item.getModel(), "value")));
            }

            @Override
            protected ListItem<Pair<V>> newItem(int index, IModel<Pair<V>> itemModel) {
                return new ListItem<Pair<V>>(index, itemModel)
                {
                    @Override
                    public IMarkupFragment getMarkup(Component child) {
                        if(child==null || !child.getId().equals("item")) return super.getMarkup(child);
                        IMarkupFragment ret = markupProvider.provideMarkup(child);
                        return ret!=null?ret:super.getMarkup(child);
                    }
                };
            }
        };

        add(listView);
        listView.setReuseItems(true);
    }

    @Override
    public void convertInput() {
        visitFormComponentsPostOrder(this, new IVisitor<FormComponent<Object>, Void>() {

            @Override
            public void component(FormComponent<Object> object,
                                  IVisit<Void> visit) {
                if(!(FunctionParametersMapPanel.this.equals(object)))
                {
                    object.updateModel();
                    visit.dontGoDeeper();
                }
            }
        });

        Map<String, V> converted = new HashMap<String, V>();
        for(Pair<V> pair: getData())
        {
            converted.put(pair.getKey(), pair.getValue());
        }
        setConvertedInput(converted);
    }

    public List<Pair<V>> getData() {
        if(data==null)
        {
            this.data = new ArrayList<Pair<V>>();
            Map<String, V> data = getConvertedInput();
            if(data==null) data = getModelObject();
            if(data!=null)
            {
                for(Map.Entry<String, V> entry : data.entrySet())
                {
                    this.data.add(new Pair<V>(entry));
                }
            }
        }
        return data;
    }

    protected static class Pair<V> implements Map.Entry<String, V>, Serializable
    {
        private String key;
        private V value;

        public Pair()
        {

        }

        public Pair(Map.Entry<String, V> entry)
        {
            this(entry.getKey(), entry.getValue());
        }

        public Pair(String key, V value)
        {
            setKey(key);
            setValue(value);
        }

        @Override
        public String getKey() {
            return key;
        }

        public void setKey(String key)
        {
            this.key = key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            this.value = value;
            return value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Pair other = (Pair) obj;
            if (key == null) {
                if (other.key != null)
                    return false;
            } else if (!key.equals(other.key))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "Pair: "+key+"="+value;
        }

    }

}
