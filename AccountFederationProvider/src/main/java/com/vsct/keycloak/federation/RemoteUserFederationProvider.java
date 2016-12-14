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

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import org.keycloak.credential.CredentialInput;
import org.keycloak.models.*;

import java.rmi.Remote;
import java.util.*;

/**
 * Remote API based user federation provider.
 *
 * @author Scott Rossillo
 */
public class RemoteUserFederationProvider implements UserFederationProvider {

    public static final String USERID = "userId";
    public static final String NEWUSER = "NEWUSER";

    private static final Logger LOG = Logger.getLogger(RemoteUserFederationProvider.class.getName());
    private static final Set<String> supportedCredentialTypes = Collections.singleton(UserCredentialModel.PASSWORD);

    private KeycloakSession session;
    private UserFederationProviderModel model;
    private static final Map<String, FederatedUserService> federatedUserServices = new HashMap<>();

    static {
        federatedUserServices.put("VSC", new FederatedUserServiceImpl());
    }

    protected RemoteUserFederationProvider(KeycloakSession session, UserFederationProviderModel model) {
        this.session = session;
        this.model = model;
    }

    @Override
    public boolean synchronizeRegistrations() {
        return true;
    }

    @Override
    public UserModel register(RealmModel realm, UserModel user) {
        LOG.warnf("User registration for [%s, %s, %s, %s]", user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName());
        user.setSingleAttribute(RemoteUserFederationProvider.NEWUSER, Boolean.TRUE.toString());
        return proxy(realm, user, null);
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        FederatedUserModel remote = federatedUserServices.get(realm.getName()).getUserDetails(username);
        if (remote == null) {
            return null;
        }

        return importUserFromRemote(realm, remote);
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        FederatedUserModel remote = federatedUserServices.get(realm.getName()).getUserDetails(email);
        if (remote == null) {
            return null;
        }

        if (session.userStorage().getUserByUsername(remote.getEmail(), realm) != null) {
            throw new ModelDuplicateException("User with username '" + remote.getEmail() + "' already exists in Keycloak. It conflicts with remote user with email '" + email + "'");
        }

        return importUserFromRemote(realm, remote);
    }

    @Override
    public List<UserModel> searchByAttributes(Map<String, String> attributes, RealmModel realm, int maxResults) {
        LOG.debug("In searchByAttributes(): " + attributes);
        List<UserModel> searchResults = new LinkedList<>();

        List<FederatedUserModel> remoteUsers = federatedUserServices.get(realm.getName()).searchByAttributes(attributes);
        for (FederatedUserModel remote : remoteUsers) {
            if (session.userStorage().getUserByUsername(remote.getEmail(), realm) == null) {
                UserModel imported = importUserFromRemote(realm, remote);
                searchResults.add(imported);
            }
        }

        return searchResults;
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
        return Collections.emptyList();
    }

    @Override
    public void preRemove(RealmModel realm) {
        // no-op
    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {
        // no-op
    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {
        // no-op
    }

    @Override
    public Set<String> getSupportedCredentialTypes() {
        return supportedCredentialTypes;
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {

    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return getSupportedCredentialTypes().contains(credentialType);
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        return false;
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return getSupportedCredentialTypes().contains(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!(input instanceof UserCredentialModel)) return false;

        String password = ((UserCredentialModel) input).getValue();
        if (input.getType().equals(UserCredentialModel.PASSWORD) && !session.userCredentialManager().isConfiguredLocally(realm, user, UserCredentialModel.PASSWORD) && !StringUtils.isEmpty(password)) {
            return validPassword(realm, user, password);
        } else {
            return false; // invalid cred type
        }
    }

    public boolean validPassword(RealmModel realm, UserModel user, String password) {
        FederatedUserModel remote = loadAndValidateUser(realm, user);
        return federatedUserServices.get(realm.getName()).validateLogin(remote.getEmail(), password);
    }

    @Override
    public CredentialValidationOutput validCredentials(RealmModel realm, UserCredentialModel credential) {
        return CredentialValidationOutput.failed();
    }

    @Override
    public UserModel validateAndProxy(RealmModel realm, UserModel local) {
        LOG.warnf("User validation and proxy for [%s, %s, %s, %s, %s]", local.getUsername(), local.getEmail(), local.getFirstName(), local.getLastName(), local.getFirstAttribute(RemoteUserFederationProvider.NEWUSER));
        if(Boolean.TRUE.toString().equals(local.getFirstAttribute(RemoteUserFederationProvider.NEWUSER))) {
            FederatedUserModel remote = federatedUserServices.get(realm.getName()).addUser(local.getEmail(), null, local.getFirstName(), local.getLastName());
            local.setSingleAttribute(RemoteUserFederationProvider.USERID, remote.getUserId());
            local.removeAttribute(RemoteUserFederationProvider.NEWUSER);
        }

        FederatedUserModel remote = loadAndValidateUser(realm, local);
        if (remote == null) {
            return null;
        }

        return proxy(realm, local, remote);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel local) {
        LOG.debugf("Checking if user is valid: %s", local);
        return loadAndValidateUser(realm, local) != null;
    }

    protected UserModel importUserFromRemote(RealmModel realm, FederatedUserModel remote) {
        String email = remote.getEmail();
        UserModel imported = session.userStorage().addUser(realm, email);

        imported.setFederationLink(model.getId());
        imported.setEnabled(remote.isEnabled());
        imported.setEmail(email);
        imported.setEmailVerified(remote.isEmailVerified());
        //imported.setFirstName(remote.getFirstName());
        //imported.setLastName(remote.getLastName());
        imported.setSingleAttribute(RemoteUserFederationProvider.USERID, remote.getUserId());

        /*if (remote.getAttributes() != null) {
            Map<String, List<String>> attributes = remote.getAttributes();
            for (String attributeName : attributes.keySet())
                imported.setAttribute(attributeName, attributes.get(attributeName));
        }*/

        if (remote.getRoles() != null) {
            for (String role : remote.getRoles()) {
                RoleModel roleModel = realm.getRole(role);
                if (roleModel != null) {
                    imported.grantRole(roleModel);
                    LOG.infof("Granted user %s, role %s", email, role);
                }
            }
        }


        LOG.debugf("Imported new user from Remote to Keycloak DB. Username: [%s], Email: [%s], USERID: [%s]", imported.getUsername(), imported.getEmail(), remote.getUserId());
        return proxy(realm, imported, remote);
    }

    /**
     * @param local
     * @return ldapUser corresponding to local user or null if user is no longer in LDAP
     */
    protected FederatedUserModel loadAndValidateUser(RealmModel realm, UserModel local) {
        FederatedUserModel user = federatedUserServices.get(realm.getName()).getUserDetails(local.getEmail());
        if (user == null) {
            LOG.warnf("no user match from Remote with username [%s], DB = %s", local.getUsername(), federatedUserServices.get(realm.getName()));
            return null;
        }

        if (!user.getUserId().equals(local.getFirstAttribute(RemoteUserFederationProvider.USERID))) {
            LOG.warnf("Local User invalid. ID doesn't match. ID from Remote [%s], ID from local DB: [%s]", user.getUserId(), local.getFirstAttribute(RemoteUserFederationProvider.USERID));
            return null;
        }

        return user;
    }

    protected UserModel proxy(RealmModel realm, UserModel local, FederatedUserModel remote) {
        return local;
    }

    @Override
    public void close() {
        // no-op
    }
}