/*
 * Copyright (C) 2016 VSCT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vsct.keycloak.federation;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.*;

import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class RemoteUserFederationProviderFactory implements UserFederationProviderFactory {

    private final static Logger LOGGER = Logger.getLogger(RemoteUserFederationProviderFactory.class);

    private static final String PROVIDER_NAME = "CCL";

    private static final Set<String> configOptions = new HashSet<String>();

    static {

    }

    @Override
    public UserFederationProvider getInstance(KeycloakSession session, UserFederationProviderModel model) {
        return (UserFederationProvider) Proxy.newProxyInstance(MethodLoggerHandler.class.getClassLoader(), new Class[]{UserFederationProvider.class}, new MethodLoggerHandler(new RemoteUserFederationProvider(session, model)));
    }

    /**
     * List the configuration options to render and display in the admin console's generic management page for this
     * plugin
     *
     * @return
     */
    @Override
    public Set<String> getConfigurationOptions() {
        return configOptions;
    }

    @Override
    public UserFederationProvider create(KeycloakSession session) {
        return null;
    }

    /**
     * You can import additional plugin configuration from keycloak-server.json here.
     *
     * @param config
     */
    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public UserFederationSyncResult syncAllUsers(KeycloakSessionFactory sessionFactory, final String realmId, final UserFederationProviderModel model) {
        return new UserFederationSyncResult();
    }

    @Override
    public UserFederationSyncResult syncChangedUsers(KeycloakSessionFactory sessionFactory, final String realmId, final UserFederationProviderModel model, Date lastSync) {
        return syncAllUsers(sessionFactory, realmId, model);
    }

    /**
     * Name of the provider.  This will show up under the "Add Provider" select box on the Federation page in the
     * admin console
     *
     * @return
     */
    @Override
    public String getId() {
        return PROVIDER_NAME;
    }
}
