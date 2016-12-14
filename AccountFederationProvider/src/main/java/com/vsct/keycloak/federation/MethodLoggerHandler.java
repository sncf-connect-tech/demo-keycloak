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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jboss.logging.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodLoggerHandler implements InvocationHandler {

    private final static Logger LOGGER = Logger.getLogger(MethodLoggerHandler.class.getName());

    private final static ToStringStyle STYLE = new TestStyle();

    private final Object proxied;

    public MethodLoggerHandler(Object proxied) {
        this.proxied = proxied;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            Object returned = method.invoke(proxied, args);
            LOGGER.info(method.toString() + " --> args: " + ToStringBuilder.reflectionToString(args, STYLE) + ", return: " + ToStringBuilder.reflectionToString(returned, STYLE));
            return returned;
        } catch (InvocationTargetException e) {
            final Throwable t = e.getCause();
            LOGGER.error(method.toString() + " --> args: " + ToStringBuilder.reflectionToString(args, STYLE) + ", exception: " + ToStringBuilder.reflectionToString(t, STYLE));
            throw t;
        }
    }

    public static class TestStyle extends ToStringStyle {
        TestStyle() {
            super();
            this.setContentStart("[");
            this.setFieldSeparator("  ");
            this.setFieldSeparatorAtStart(true);
            this.setContentEnd("]");
        }

        @Override
        public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) {
            if (value != null) {
                super.append(buffer, fieldName, value, fullDetail);
            }
        }

    }
}