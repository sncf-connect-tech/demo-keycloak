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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.*;

public class FederatedUserModel implements Serializable {
    private String userId;
    private String email;
    private boolean emailVerified = true;
    private boolean enabled = true;
    private String firstName;
    private String lastName;
    private Map<String, List<String>> attributes = new HashMap<>();
    private Set<String> roles = new HashSet<>();
    private String password;

    public FederatedUserModel() {
    }

    public FederatedUserModel(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    FederatedUserModel(String userId, String email, String password, String firstName, String lastName) {
        this(userId, email);
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    String getPassword() {
        return password;
    }

    void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Map<String, List<String>> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, List<String>> attributes) {
        this.attributes = attributes;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof  FederatedUserModel)) {
            return false;
        }
        final FederatedUserModel user = (FederatedUserModel) obj;
        return new EqualsBuilder().append(this.userId, user.userId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.userId).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}