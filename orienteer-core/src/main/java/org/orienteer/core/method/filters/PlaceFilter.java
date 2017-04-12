package org.orienteer.core.method.filters;

import java.util.ArrayList;
import java.util.List;

import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.IMethodFilter;
import org.orienteer.core.method.MethodPlace;

/**
 * Filter allowed if any place overlapped 
 * 
 *
 */
public class PlaceFilter implements IMethodFilter{

	private List<MethodPlace> places;
	
	@Override
	public void setFilterData(String filterData) {
		String[] strPlaces = filterData.split("\\|");
		if (strPlaces.length>0){
			places = new ArrayList<MethodPlace>(strPlaces.length);
			for (String strPl : strPlaces) {
				MethodPlace pl = MethodPlace.valueOf(strPl);
				if (pl!=null){
					places.add(pl);
				}
			}
		}
	}

	@Override
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject) {
		if (places!=null && dataObject.getPlace()!=null){
			for (MethodPlace methodPlace : places) {
				if (methodPlace.equals(dataObject.getPlace())){
					return true;
				}
			}
		}
		return false;
	}

}
