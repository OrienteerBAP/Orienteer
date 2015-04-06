/**
 * Copyright (C) 2015 Ilia Naryzhny (phantom@ydn.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.orienteer;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.guice.GuiceInjectorHolder;
import org.apache.wicket.markup.html.IPackageResourceGuard;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.settings.IRequestCycleSettings;
import org.orienteer.components.properties.UIVisualizersRegistry;
import org.orienteer.hooks.CalculablePropertiesHook;
import org.orienteer.hooks.ReferencesConsistencyHook;
import org.orienteer.modules.IOrienteerModule;
import org.orienteer.modules.ModuledDataInstallator;
import org.orienteer.modules.OrienteerLocalizationModule;
import org.orienteer.modules.PerspectivesModule;
import org.orienteer.modules.UpdateDefaultSchemaModule;
import org.orienteer.services.IOClassIntrospector;
import org.orienteer.web.BasePage;
import org.orienteer.web.HomePage;
import org.orienteer.web.LoginPage;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

import ru.ydn.wicket.wicketorientdb.EmbeddOrientDbApplicationListener;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.rest.OrientDBHttpAPIResource;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

import de.agilecoders.wicket.webjars.WicketWebjars;
import de.agilecoders.wicket.webjars.settings.IWebjarsSettings;
import de.agilecoders.wicket.webjars.settings.WebjarsSettings;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 *
 * @see org.orienteer.Start#main(String[])
 */
public class OrienteerWebApplication extends OrientDbWebApplication {

    private Map<String, IOrienteerModule> registeredModules = new LinkedHashMap<String, IOrienteerModule>();

    @Inject
    private IWebjarsSettings webjarSettings;

    @Inject
    @Named("orienteer.production")
    private boolean production;

    @Inject
    @Named("orientdb.embedded")
    private boolean embedded;

    @Inject(optional = true)
    @Named("wicket.render.strategy")
    private IRequestCycleSettings.RenderStrategy renderStrategy;

    @Inject
    public OrienteerWebApplication() {
        setConfigurationType(production ? RuntimeConfigurationType.DEPLOYMENT : RuntimeConfigurationType.DEVELOPMENT);
    }

    @Inject
    @Override
    public void setOrientDbSettings(IOrientDbSettings orientDbSettings) {
        super.setOrientDbSettings(orientDbSettings);
    }

    public static OrienteerWebApplication get() {
        return (OrienteerWebApplication) WebApplication.get();
    }

    public static OrienteerWebApplication lookupApplication() {
        return lookupApplication(OrienteerWebApplication.class);
    }

    @Override
    public Class<? extends WebPage> getHomePage() {
        return HomePage.class;
    }

    @Override
    protected Class<? extends OrienteerWebSession> getWebSessionClass() {
        return OrienteerWebSession.class;
    }

    /**
     * @see org.apache.wicket.Application#init()
     */
    @Override
    public void init() {
        super.init();
        if (embedded) {
            getApplicationListeners().add(new EmbeddOrientDbApplicationListener(OrienteerWebApplication.class.getResource("db.config.xml")) {

                @Override
                public void onAfterServerStartupAndActivation(OrientDbWebApplication app)
                        throws Exception {
                    IOrientDbSettings settings = app.getOrientDbSettings();
                    ODatabaseDocumentTx db = new ODatabaseDocumentTx(settings.getDBUrl());
                    if (!db.exists()) {
                        db = db.create();
                    }
                    if (db.isClosed()) {
                        db.open(settings.getDBInstallatorUserName(), settings.getDBInstallatorUserPassword());
                    }
                    db.getMetadata().load();
                    db.close();
                }

            });
        }
        WicketWebjars.install(this, webjarSettings);
        new AnnotatedMountScanner().scanPackage("org.orienteer.web").mount(this);
        getResourceBundles().addCssBundle(BasePage.class, "orienteer.css", BasePage.SB_ADMIN_CSS, BasePage.ORIENTEER_CSS);
        getMarkupSettings().setStripWicketTags(true);
        getResourceSettings().setThrowExceptionOnMissingResource(false);
        IPackageResourceGuard packageResourceGuard = getResourceSettings().getPackageResourceGuard();
        if (packageResourceGuard instanceof SecurePackageResourceGuard) {
            SecurePackageResourceGuard guard = (SecurePackageResourceGuard) packageResourceGuard;
            //Allow to access only to woff2 - new format from bootstrap
            guard.addPattern("+*.woff2");
        }

        getApplicationListeners().add(new ModuledDataInstallator());
        registerModule(OrienteerLocalizationModule.class);
        registerModule(UpdateDefaultSchemaModule.class);
        registerModule(PerspectivesModule.class);
        getOrientDbSettings().getORecordHooks().add(new CalculablePropertiesHook());
        getOrientDbSettings().getORecordHooks().add(new ReferencesConsistencyHook());
        mountOrientDbRestApi();
        if (renderStrategy != null) {
            getRequestCycleSettings().setRenderStrategy(renderStrategy);
        }
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class;
    }

    public Injector getInjector() {
        return getMetaData(GuiceInjectorHolder.INJECTOR_KEY).getInjector();
    }

    public <T> T getServiceInstance(Class<T> serviceType) {
        return getInjector().getInstance(serviceType);
    }

    public ODatabaseDocument getDatabase() {
        return OrientDbWebSession.get().getDatabase();
    }

    public Map<String, IOrienteerModule> getRegisteredModules() {
        return registeredModules;
    }

    public <M extends IOrienteerModule> M registerModule(Class<M> moduleClass) {
        M module = getServiceInstance(moduleClass);
        registeredModules.put(module.getName(), module);
        return module;
    }

    public IOrienteerModule getModuleByName(String name) {
        return registeredModules.get(name);
    }

    public UIVisualizersRegistry getUIVisualizersRegistry() {
        return getServiceInstance(UIVisualizersRegistry.class);
    }

    public IOClassIntrospector getOClassIntrospector() {
        return getServiceInstance(IOClassIntrospector.class);
    }

}
