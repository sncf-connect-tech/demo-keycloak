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
import org.keycloak.models.UserModel;

import java.util.*;

public class FederatedUserServiceImpl implements FederatedUserService {

    private final Map<String, FederatedUserModel> emails = new HashMap<>();
    private final Map<String, FederatedUserModel> firstNames = new HashMap<>();
    private final Map<String, FederatedUserModel> lastNames = new HashMap<>();

    FederatedUserServiceImpl() {
        addUser("test1", "test1@test.com", "password", "test1", "test1");
        addUser("test2", "test2@test.com", "password", "test2", "test2");
        addUser("test3", "test3@test.com", "password", "test3", "test3");
        addUser("test4", "test4@test.com", "password", "test4", "test4");
        addUser("test11", "test11@test.com", "password", "test11", "test11");
        addUser("test12", "test12@test.com", "password", "test12", "test12");
        addUser("test13", "test13@test.com", "password", "test13", "test13");
        addUser("test14", "test14@test.com", "password", "test14", "test14");
        addUser("test21", "test21@test.com", "password", "test21", "test21");
        addUser("test23", "test23@test.com", "password", "test23", "test23");
    }

    @Override
    public FederatedUserModel getUserDetails(String email) {
        return this.emails.get(email);
    }

    @Override
    public boolean validateUserExists(String email) {
        return this.emails.get(email) != null;
    }

    @Override
    public boolean validateLogin(String email, String password) {
        String localPassword = getUserDetails(email).getPassword();

        if(!StringUtils.isEmpty(localPassword)) {
            return localPassword.equals(password);
        }

        return false;
    }

    @Override
    public List<FederatedUserModel> searchByAttributes(Map<String, String> attributes) {
        final List<FederatedUserModel> results = new ArrayList<>();

        if (attributes.containsKey(UserModel.USERNAME)) {
            final FederatedUserModel user = getUserDetails(attributes.get(UserModel.USERNAME));
            if (user != null) {
                results.add(user);
            }
        }

        if (attributes.containsKey(UserModel.EMAIL)) {
            final FederatedUserModel user = getUserDetails(attributes.get(UserModel.EMAIL));
            if (user != null) {
                results.add(user);
            }
        }

        if (attributes.containsKey(UserModel.FIRST_NAME) || attributes.containsKey(UserModel.LAST_NAME)) {
            FederatedUserModel userFirstName = null;
            if (attributes.containsKey(UserModel.FIRST_NAME)) {
                userFirstName = firstNames.get(attributes.get(UserModel.FIRST_NAME));
            }

            FederatedUserModel userLastName = null;
            if (attributes.containsKey(UserModel.LAST_NAME)) {
                userLastName = firstNames.get(attributes.get(UserModel.LAST_NAME));
            }

            if (attributes.containsKey(UserModel.FIRST_NAME) && attributes.containsKey(UserModel.LAST_NAME) && userFirstName != null && userLastName != null && userFirstName.equals(userLastName)) {
                results.add(userFirstName);
            } else if (attributes.containsKey(UserModel.FIRST_NAME) && userFirstName != null) {
                results.add(userFirstName);
            } else if (attributes.containsKey(UserModel.LAST_NAME) && userLastName != null) {
                results.add(userLastName);
            }
        }

        return results;
    }

    protected FederatedUserModel addUser(String uid, String email, String password, String firstName, String lastName) {
        FederatedUserModel user = new FederatedUserModel(uid, email, password, firstName, lastName);
        indexUser(user);
        return user;
    }

    @Override
    public FederatedUserModel addUser(String email, String password, String firstName, String lastName) {
        return addUser(getUID(), email, password, firstName, lastName);
    }

    private String getUID() {
        return UUID.randomUUID().toString();
    }

    private void indexUser(FederatedUserModel user) {
        emails.put(user.getEmail(), user);
        firstNames.put(user.getFirstName(), user);
        lastNames.put(user.getLastName(), user);
    }

    @Override
    public String toString() {
        return this.emails.toString();
    }
}
