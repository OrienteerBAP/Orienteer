package org.orienteer.etl.component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import org.orienteer.core.OrienteerWebApplication;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabasePool;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.OConfigurationException;
import com.orientechnologies.orient.core.exception.OSchemaException;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClassImpl;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.ORecordDuplicatedException;
import com.orientechnologies.orient.etl.OETLPipeline;
import com.orientechnologies.orient.etl.context.OETLContext;
import com.orientechnologies.orient.etl.context.OETLContextWrapper;
import com.orientechnologies.orient.etl.loader.OETLAbstractLoader;
import com.orientechnologies.orient.etl.loader.OETLLoaderException;

import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

/**
 * Loader for OrientDB ETL to Orienteer
 *
 */
public class OETLOrienteerLoader extends OETLAbstractLoader {

	private static final String NOT_DEF = "not_defined";
	public ODatabasePool pool;
	public OrientDB orient;
	protected String clusterName;
	protected String className;
	private List<ODocument> classes;
	private List<ODocument> indexes;
	protected OClass schemaClass;
	protected String dbURL;
	protected String dbUser = "admin";
	protected String dbPassword = "admin";
	protected String serverUser = NOT_DEF;
	protected String serverPassword = NOT_DEF;
	protected boolean dbAutoDropIfExists = false;
	protected boolean dbAutoCreateProperties = false;
	protected boolean useLightweightEdges = false;
	protected boolean standardElementConstraints = true;
	protected boolean tx = false;
	protected int batchCommitSize = 0;
	protected AtomicLong batchCounter = new AtomicLong(0);
	protected boolean wal = true;
	protected boolean txUseLog = false;
	protected boolean skipDuplicates = false;

	public OETLOrienteerLoader() {}

	public ODatabasePool getPool() {
		return pool;
	}

	@Override
	public void load(ODatabaseDocument db, final Object input, OCommandContext context) {

		if (input == null)
			return;

		if (dbAutoCreateProperties) {
			autoCreateProperties(db, input);
		}

		if (tx) {
			if (!db.getTransaction().isActive()) {
				db.begin();
				db.getTransaction().setUsingLog(txUseLog);
			}
		}

		if (input instanceof OVertex) {
			final OVertex v = (OVertex) input;

			try {
				v.save(clusterName);
			} catch (ORecordDuplicatedException e) {
				if (!skipDuplicates) {
					throw e;
				}
			}
		} else if (input instanceof ODocument) {

			final ODocument doc = (ODocument) input;

			if (className != null) {
				doc.setClassName(className);
			}

			if (clusterName != null) {
				db.save(doc, clusterName);
			} else if (doc.getClassName() != null) {
				db.save(doc);
			} else {
				OETLContextWrapper.getInstance().getMessageHandler().debug(this,
						"The ETL loader is not explicitly saving the record %s - no class or cluster set",
						doc.toString());
			}

		} else {
			OETLContextWrapper.getInstance().getMessageHandler().error(this, "input type not supported::  %s", input.getClass());
		}

		progress.incrementAndGet();

		// DO BATCH COMMIT if on TX

		if (tx && batchCommitSize > 0 && batchCounter.get() > batchCommitSize) {
			synchronized (this) {
				if (batchCommitSize > 0 && batchCounter.get() > batchCommitSize) {
					log(Level.FINE, "committing document batch %d", progress.get());
					db.commit();
					db.begin();
					db.getTransaction().setUsingLog(txUseLog);
					batchCounter.set(0);
				}
			}
		} else {
			batchCounter.incrementAndGet();
		}
	}

	protected void autoCreateProperties(ODatabaseDocument db, Object input) {
		if (input instanceof ODocument) {
			autoCreatePropertiesOnDocument(db, (ODocument) input);
		} else if (input instanceof OVertex) {
			autoCreatePropertiesOnElement(db, (OVertex) input);
		}
	}

	protected void autoCreatePropertiesOnElement(ODatabaseDocument db, OVertex element) {

		final OClass cls;
		Optional<OClass> schemaType = element.getSchemaType();
		final String clsName = schemaType.get().getName();

		if (clsName != null)
			cls = getOrCreateClass(db, clsName, element.getSchemaType().get().getName());
		else
			throw new IllegalArgumentException("No class defined on graph element: " + element);

		for (String f : element.getPropertyNames()) {
			final String newName = transformFieldName(f);
			final String fName = newName != null ? newName : f;

			OProperty p = cls.getProperty(fName);
			if (p == null) {
				final Object fValue = element.getProperty(f);
				createProperty(cls, fName, fValue);
				if (newName != null) {
					// REPLACE IT
					element.removeProperty(f);
					element.setProperty(newName, fValue);
				}
			}
		}
	}

	protected void autoCreatePropertiesOnDocument(ODatabaseDocument db, ODocument doc) {
		final OClass cls;
		if (className != null)
			cls = getOrCreateClass(db, className, null);
		else
			cls = doc.getSchemaClass();

		for (String f : doc.fieldNames()) {
			final String newName = transformFieldName(f);
			final String fName = newName != null ? newName : f;

			OProperty p = cls.getProperty(fName);
			if (p == null) {
				final Object fValue = doc.field(f);
				createProperty(cls, fName, fValue);
				if (newName != null) {
					// REPLACE IT
					doc.removeField(f);
					doc.field(newName, fValue);
				}
			}
		}
	}

	@Override
	public String getUnit() {
		return "items";
	}

	@Override
	public void rollback(ODatabaseDocument db) {
		if (tx) {
			if (db.getTransaction().isActive())
				db.rollback();
		}
	}

	protected OClass getOrCreateClass(ODatabaseDocument db, final String iClassName, final String iSuperClass) {
		OClass cls = getOrCreateClassOnDocument(db, iClassName, iSuperClass);
		db.activateOnCurrentThread();
		if (clusterName != null) {
			int clusterIdByName = db.getClusterIdByName(clusterName);
			if (clusterIdByName == -1) {
				log(Level.FINE, "add cluster :: " + clusterName);
				cls.addCluster(clusterName);
			}
		}
		return cls;
	}
	private OClass getOrCreateClassOnDocument(ODatabaseDocument documentDatabase, String iClassName,
			String iSuperClass) {
		OClass cls; // DOCUMENT
		if (documentDatabase.getMetadata().getSchema().existsClass(iClassName))
			cls = documentDatabase.getMetadata().getSchema().getClass(iClassName);
		else {
			if (iSuperClass != null) {
				final OClass superClass = documentDatabase.getMetadata().getSchema().getClass(iSuperClass);
				if (superClass == null)
					throw new OETLLoaderException("Cannot find super class '" + iSuperClass + "'");

				cls = documentDatabase.getMetadata().getSchema().createClass(iClassName, superClass);
				log(Level.FINE, "- OrientDBLoader: created class '%s' extends '%s'", iClassName, iSuperClass);
			} else {
				cls = documentDatabase.getMetadata().getSchema().createClass(iClassName);
				log(Level.FINE, "- OrientDBLoader: created class '%s'", iClassName);
			}
		}
		return cls;
	}

	private String transformFieldName(String f) {
		final char first = f.charAt(0);
		if (Character.isDigit(first))
			return "field" + Character.toUpperCase(first) + (f.length() > 1 ? f.substring(1) : "");
		return null;
	}

	protected void createProperty(final OClass cls, final String f, final Object fValue) {
		if (fValue != null) {
			final OType fType = OType.getTypeByClass(fValue.getClass());

			try {
				cls.createProperty(f, fType);
			} catch (OSchemaException e) {
			}

			log(Level.FINE, "created property [%s.%s] of type [%s]", cls.getName(), f, fType);
		}
	}

	@Override
	public ODocument getConfiguration() {
		return new ODocument().fromJSON("{parameters:[" + "{dbUrl:{optional:false,description:'Database URL'}},"
				+ "{dbUser:{optional:true,description:'Database user, default is admin'}},"
				+ "{dbPassword:{optional:true,description:'Database password, default is admin'}},"
				+ "{class:{optional:true,description:'Record class name'}},"
				+ "{tx:{optional:true,description:'Transaction mode: true executes in transaction, false for atomic operations'}},"
				+ "{dbAutoCreate:{optional:true,description:'Auto create the database if not exists. Default is true'}},"
				+ "{dbAutoCreateProperties:{optional:true,description:'Auto create properties in schema'}},"
				+ "{dbAutoDropIfExists:{optional:true,description:'Auto drop the database if already exists. Default is false.'}},"
				+ "{batchCommit:{optional:true,description:'Auto commit every X items. This speed up creation of edges.'}},"
				+ "{wal:{optional:true,description:'Use the WAL (Write Ahead Log)'}},"
				+ "{useLightweightEdges:{optional:true,description:'Enable/Disable LightweightEdges in Graphs. Default is false'}},"
				+ "{standardElementConstraints:{optional:true,description:'Enable/Disable Standard Blueprints constraints on names. Default is true'}},"
				+ "{cluster:{optional:true,description:'Cluster name where to store the new record'}},"
				+ "{settings:{optional:true,description:'OrientDB settings as a map'}},"
				+ "{classes:{optional:true,description:'Classes used. It assure the classes exist or in case create them'}},"
				+ "{indexes:{optional:true,description:'Indexes used. It assure the indexes exist or in case create them'}}],"
				+ "input:['OrientVertex','ODocument']}");
	}

	@Override
	public void configure(final ODocument conf, final OCommandContext iContext) {
		super.configure(conf, iContext);

		if (conf.containsField("dbURL"))
			dbURL = (String) resolve(conf.field("dbURL"));

		if (conf.containsField("dbUser"))
			dbUser = (String) resolve(conf.field("dbUser"));
		if (conf.containsField("dbPassword"))
			dbPassword = (String) resolve(conf.field("dbPassword"));

		if (conf.containsField("serverUser"))
			serverUser = (String) resolve(conf.field("serverUser"));
		if (conf.containsField("serverPassword"))
			serverPassword = (String) resolve(conf.field("serverPassword"));

		if (conf.containsField("tx"))
			tx = conf.<Boolean>field("tx");
		if (conf.containsField("wal"))
			wal = conf.<Boolean>field("wal");
		if (conf.containsField("txUseLog"))
			txUseLog = conf.<Boolean>field("txUseLog");
		if (conf.containsField("batchCommit"))
			batchCommitSize = conf.<Integer>field("batchCommit");
		if (conf.containsField("dbAutoDropIfExists"))
			dbAutoDropIfExists = conf.<Boolean>field("dbAutoDropIfExists");
		if (conf.containsField("dbAutoCreateProperties"))
			dbAutoCreateProperties = conf.<Boolean>field("dbAutoCreateProperties");
		if (conf.containsField("useLightweightEdges"))
			useLightweightEdges = conf.<Boolean>field("useLightweightEdges");
		if (conf.containsField("standardElementConstraints"))
			standardElementConstraints = conf.<Boolean>field("standardElementConstraints");

		if (conf.containsField("skipDuplicates"))
			skipDuplicates = conf.field("skipDuplicates");

		clusterName = conf.field("cluster");
		className = conf.field("class");
		indexes = conf.field("indexes");
		classes = conf.field("classes");

		if (conf.containsField("settings")) {
			final ODocument settings = conf.field("settings");
			settings.setAllowChainedAccess(false);
			for (String s : settings.fieldNames()) {
				final OGlobalConfiguration v = OGlobalConfiguration.findByKey(s);
				if (v != null)
					v.setValue(settings.field(s));
			}
		}

		pool = getDatabasePool();
	}

	@Override
	public void begin(ODatabaseDocument db) {
	}
	
	protected ODatabasePool getDatabasePool() {
		if(pool!=null) return pool;
		IOrientDbSettings settings = OrienteerWebApplication.lookupApplication().getOrientDbSettings();
		OrientDbWebSession session = OrientDbWebSession.exists()?OrientDbWebSession.get():null;
		String username;
		String password;
		if(session!=null && session.isSignedIn())
		{
			username = session.getUsername();
			password = session.getPassword();
		}
		else
		{
			username = settings.getAdminUserName();
			password = settings.getAdminPassword();
		}
		return settings.getContext().cachedPool(settings.getDbName(), username, password);
	}

	@Override
	public synchronized void beginLoader(OETLPipeline pipeline) {
		try(ODatabaseDocument db = pool.acquire()) {
			db.activateOnCurrentThread();
			createSchema((ODatabaseDocumentInternal) db);
			db.close();
			pipeline.setPool(pool);
		}
	}

	private void createSchema(ODatabaseDocumentInternal db) {

		if (classes != null) {
			for (ODocument cls : classes) {
				schemaClass = getOrCreateClass(db, cls.field("name"), cls.field("extends"));

				Integer clusters = cls.field("clusters");
				if (clusters != null)
					OClassImpl.addClusters(schemaClass, clusters);

				log(Level.FINE, "%s: found %d %s in class '%s'", getName(), schemaClass.count(), getUnit(), className);
			}
		}
		if (className != null) {
			schemaClass = getOrCreateClass(db, className, null);
			log(Level.FINE, "%s: found %d %s in class '%s'", getName(), schemaClass.count(), getUnit(), className);
		}
		if (indexes != null) {
			for (ODocument idx : indexes) {
				OIndex index;

				final ODocument metadata = (ODocument) resolve(idx.field("metadata"));
				log(Level.FINE, "%s: found metadata field '%s'", getName(), metadata);

				String idxName = (String) resolve(idx.field("name"));
				if (idxName != null) {
					index = db.getMetadata().getIndexManagerInternal().getIndex(db, idxName);
					if (index != null)
						// ALREADY EXISTS
						continue;
				}

				final String idxClass = (String) resolve(idx.field("class"));
				if (idxClass == null)
					throw new OConfigurationException("Index 'class' missed in OrientDB Loader");

				final OClass cls = getOrCreateClass(db, idxClass, null);
				final String idxType = idx.field("type");
				if (idxType == null)
					throw new OConfigurationException(
							"Index 'type' missed in OrientDB Loader for index '" + idxName + "'");

				final String algorithm = idx.field("algorithm");

				final List<String> idxFields = idx.field("fields");
				if (idxFields == null)
					throw new OConfigurationException("Index 'fields' missed in OrientDB Loader");

				String[] fields = new String[idxFields.size()];
				for (int f = 0; f < fields.length; ++f) {
					final String fieldName = idxFields.get(f);

					final String[] fieldNameParts = fieldName.split(":");

					if (!cls.existsProperty(fieldNameParts[0])) {
						// CREATE PROPERTY AUTOMATICALLY

						if (fieldNameParts.length < 2)
							throw new OConfigurationException(
									"Index field type missed in OrientDB Loader for field '" + fieldName + "'");

						final String fieldType = fieldNameParts[1].toUpperCase(Locale.ENGLISH);
						final OType type = OType.valueOf(fieldType);

						cls.createProperty(fieldNameParts[0], type);
						log(Level.FINE, "- OrientDBLoader: created property '%s.%s' of type: %s", idxClass,
								fieldNameParts[0], fieldNameParts[1]);
					}

					fields[f] = fieldNameParts[0];
				}

				if (idxName == null) {
					// CREATE INDEX NAME
					idxName = idxClass + ".";
					for (int i = 0; i < fields.length; ++i) {
						if (i > 0)
							idxName += '_';
						idxName += fields[i];
					}
				}

				index = db.getMetadata().getIndexManagerInternal().getIndex(db, idxName);
				if (index != null)
					// ALREADY EXISTS
					continue;

				index = cls.createIndex(idxName, idxType, null, metadata, algorithm, fields);
				log(Level.FINE, "- OrientDocumentLoader: created index '%s' type '%s' against Class '%s', fields %s",
						idxName, idxType, idxClass, idxFields);
			}
		}
	}

	@Override
	public void end() {
	}

	@Override
	public void close() {

	}

	@Override
	public String getName() {
		return "orienteer";
	}

}
