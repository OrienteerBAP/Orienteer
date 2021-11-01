package org.orienteer.core.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.orienteer.core.OClassDomain;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.dao.DAO;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.dao.OrienteerOClass;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.filters.PlaceFilter;
import org.orienteer.core.method.filters.WidgetTypeFilter;
import org.orienteer.transponder.annotation.EntityType;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
/**
 * OTask class for system console commands
 *
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(value = IOConsoleTask.CLASS_NAME)
@OrienteerOClass(orderOffset = 50)
public interface IOConsoleTask extends IOTask<IOTaskSessionPersisted> {
	public static final String CLASS_NAME = "OConsoleTask";
	
	public String getInput();
	public IOConsoleTask setInput(String input);

	@Override
	public default OTaskSessionRuntime<IOTaskSessionPersisted> startNewSession() {
		final IOTaskSessionPersisted otaskSession1 = DAO.create(IOTaskSessionPersisted.class);
		final String input = getInput();
		otaskSession1.setDeleteOnFinish(isAutodeleteSessions());
		otaskSession1.setTask(this);
		otaskSession1.persist();
		OTaskSessionRuntime<IOTaskSessionPersisted> runtime = new OTaskSessionRuntime<>(otaskSession1);
		try{
			Thread innerThread = new Thread(new Runnable(){
				@Override
				public void run() {
					runtime.start();
					String charset =  Charset.defaultCharset().displayName();
					if(System.getProperty("os.name").startsWith("Windows")){
						if (Charset.isSupported("cp866")){
							charset = "cp866";
						}
					}
					try {
						
						final Process innerProcess = Runtime.getRuntime().exec(input);
						runtime.setCallback(new ITaskSessionCallback() {
								
								@Override
								public void interrupt() throws Exception {
									try {
										innerProcess.exitValue();
										//There is exit code: process is finished already
									} catch (IllegalThreadStateException e) {
										//Process is active - destroying
										innerProcess.destroy();
									}
								}
							});
						try(BufferedReader reader =  new BufferedReader(new InputStreamReader(innerProcess.getInputStream(),charset))) {
							String curOutString = "";
								while ((curOutString = reader.readLine())!= null) {
									runtime.incrementCurrentProgress();
									runtime.getOTaskSessionPersisted().appendOutput(curOutString).persist();
								}
						} 
					} catch (IOException e) {
						runtime.getOTaskSessionPersisted().appendOutput(e.getMessage()).persist();
					}	
					runtime.finish();
				}
				
			});
			innerThread.start();

		} catch (Exception e) {
			runtime.finish();
		}
		return runtime.getOTaskSessionRuntime();	
	}
	
}
