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

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Proxy;

/**
 * Created by cyril_vrillaud on 16/08/2016.
 */
public class MethodLoggerHandlerTest {


    @Test
    public void testLogger() throws Exception {
        ProxyTestMock proxy = (ProxyTestMock) Proxy.newProxyInstance(MethodLoggerHandler.class.getClassLoader(), new Class[]{ProxyTestMock.class}, new MethodLoggerHandler(new ProxyTestMockImpl()));
        proxy.test1();
        proxy.test2("test2.arg1");
        proxy.test3("test3.arg1", "test3.arg2");
        Assert.assertEquals("test4.arg1", proxy.test4("test4.arg1"));
        proxy.test5("test5.arg1");
        Assert.assertEquals("test", proxy.test6());

        try {
            proxy.test7();
        } catch (Exception e) {
            Assert.assertEquals("test7", e.getMessage());
        }

        try {
            proxy.test8();
        } catch (Exception e) {
            Assert.assertEquals("test8", e.getMessage());
        }

        try {
            proxy.test9("test9");
        } catch (Exception e) {
            Assert.assertEquals("test9", e.getMessage());
        }

        try {
            proxy.test10("test10");
        } catch (Exception e) {
            Assert.assertEquals("test10", e.getMessage());
        }
    }

}