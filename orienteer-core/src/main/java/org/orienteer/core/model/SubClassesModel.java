package org.orienteer.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.ChainingModel;
import org.apache.wicket.model.IModel;
import org.orienteer.core.OrienteerWebSession;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * Model to get list of sub classes of provided one 
 */
public class SubClassesModel extends ChainingModel<List<OClass>>{
	
	private static class OClassCanBeInstanciated implements Predicate<OClass>, Serializable {

		@Override
		public boolean apply(OClass input) {
			return !input.isAbstract();
		}
		
	}
	
	private final static Predicate<OClass> CAN_BE_INSTANTIATED = new OClassCanBeInstanciated();
	
	private boolean includingRoot;
	private boolean onlyInstantiatable;

	/**
	 * Constructor for {@link SubClassesModel}
	 * @param rootClassModel model for obtaining of root class
	 * @param includingRoot should root class included into results
	 * @param onlyInstantiatable should only non abstract classes be included
	 */
	public SubClassesModel(IModel<OClass> rootClassModel, boolean includingRoot, boolean onlyInstantiatable) {
		super(rootClassModel);
		this.includingRoot = includingRoot;
		this.onlyInstantiatable = onlyInstantiatable;
	}
	
	@Override
	public List<OClass> getObject() {
		OClass oClass = getChainedModel().getObject();
		List<OClass> ret = new ArrayList<OClass>();
		if(onlyInstantiatable) {
			if(oClass!=null) {
				if(includingRoot && CAN_BE_INSTANTIATED.apply(oClass)) ret.add(oClass);
				ret.addAll(Collections2.filter(oClass.getAllSubclasses(), CAN_BE_INSTANTIATED));
			} else {
				ret.addAll(Collections2.filter(OrienteerWebSession.get().getDatabase().getMetadata().getSchema().getClasses(), CAN_BE_INSTANTIATED));
			}
		} else {
			if(oClass!=null) {
				if(includingRoot) ret.add(oClass);
				ret.addAll(oClass.getAllSubclasses());
			} else {
				ret.addAll(OrienteerWebSession.get().getDatabase().getMetadata().getSchema().getClasses());
			}
		}
		return ret;
	}
	
	@Override
	public void setObject(List<OClass> object) {
		throw new UnsupportedOperationException("Model " + getClass() +
				" does not support setObject(List<OClass>)");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public IModel<OClass> getChainedModel() {
		return (IModel<OClass>) super.getChainedModel();
	}

}
