package org.orienteer.devutils.component;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;

import ru.ydn.wicket.wicketconsole.IScriptEngineInterlayer;
import ru.ydn.wicket.wicketconsole.IScriptEngineInterlayerResult;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

public class ODBScriptEngineInterlayer implements IScriptEngineInterlayer{

	String name;
	
	public ODBScriptEngineInterlayer() {
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public IScriptEngineInterlayerResult eval(String command) {
		ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
		ODBScriptEngineInterlayerResult result = new ODBScriptEngineInterlayerResult();
		db.commit();
		OCommandSQL comm = new OCommandSQL(command);
		try{
			result.setReturnedObject(db.command(comm).execute());
			result.onUpdate();
		}catch(Exception e){
			result.setError(e.getMessage());
		}
		return result;
	}
	
}
