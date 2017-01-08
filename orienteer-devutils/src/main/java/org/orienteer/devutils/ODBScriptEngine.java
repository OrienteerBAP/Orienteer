package org.orienteer.devutils;

import java.util.regex.Pattern;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.string.Strings;

import com.google.common.base.Throwables;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.OCommandSQLParsingException;
import com.orientechnologies.orient.core.sql.query.OResultSet;

import ru.ydn.wicket.wicketconsole.IScriptEngine;
import ru.ydn.wicket.wicketconsole.ScriptResult;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;

/**
 * {@link IScriptEngine} for execution of SQL in OrientDB
 */
public class ODBScriptEngine implements IScriptEngine {
	
	private static final Pattern SELECT_FROM_PATTERN = Pattern.compile("^select\\s+from", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	@Override
	public String getName() {
		return ODBScriptEngineFactory.ENGINE_NAME;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ScriptResult eval(String command) {
		ScriptResult result = new ScriptResult(ODBScriptEngineFactory.ENGINE_NAME, command);
		if(!Strings.isEmpty(command)) {
			command = command.trim();
			try {
				if(SELECT_FROM_PATTERN.matcher(command).find()) {
					OQueryModel<ODocument> returnModel =  new OQueryModel<ODocument>(command);
					returnModel.probeOClass(10);
					result.setResultModel(returnModel);
				} else {
					ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
					OCommandSQL comm = new OCommandSQL(command);
					Object resultObject = db.command(comm).execute();
					if(resultObject instanceof OResultSet) {
						StringBuilder sb = new StringBuilder("[");
						boolean first = true;
						for(ODocument doc : ((OResultSet<ODocument>)resultObject)) {
							if(!first) sb.append(", ");
							sb.append(doc.toJSON());
							first=false;
						}
						sb.append("]");
						resultObject = sb.toString();
					}
					result.setResult(resultObject);
				}
			} catch (Exception e) {
				if(shouldBeShorted(e)) {
					result.setError(e.getMessage());
				} else {
					result.setError(Throwables.getStackTraceAsString(e));
				}
			}
		}
		return result;
	}
	
	protected boolean shouldBeShorted(Exception e) {
		return e instanceof OCommandSQLParsingException;
	}

}
