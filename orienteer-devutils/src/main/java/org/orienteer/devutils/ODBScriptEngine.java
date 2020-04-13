package org.orienteer.devutils;

import com.google.common.base.Throwables;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.OSecurityAccessException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQLParsingException;
import org.apache.wicket.util.string.Strings;
import ru.ydn.wicket.wicketconsole.IScriptContext;
import ru.ydn.wicket.wicketconsole.IScriptEngine;
import ru.ydn.wicket.wicketconsole.ScriptResult;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;

import java.util.regex.Pattern;

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
	public ScriptResult eval(String command, IScriptContext ctx) {
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
					result.start();
					result.setResult(db.command(command));
				}
			} catch (Exception e) {
				if(shouldBeShorted(e)) {
					result.setError(e.getMessage());
				} else {
					result.setError(Throwables.getStackTraceAsString(e));
				}
			} finally {
				result.finish();
			}
		}
		return result;
	}
	
	protected boolean shouldBeShorted(Exception e) {
		return e instanceof OCommandSQLParsingException || e instanceof OSecurityAccessException;
	}

}
