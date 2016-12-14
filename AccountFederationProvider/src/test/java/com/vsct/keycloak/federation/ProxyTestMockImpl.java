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

/**
 * Created by cyril_vrillaud on 21/10/2016.
 */
public class ProxyTestMockImpl implements ProxyTestMock {

    @Override
    public void test1() {
    }

    @Override
    public void test2(String arg1) {
    }

    @Override
    public void test3(String arg1, String arg2) {
    }

    @Override
    public String test4(String arg1) {
        return arg1;
    }

    @Override
    public void test5(Object arg1) {
    }

    @Override
    public Object test6() {
        return "test";
    }

    @Override
    public void test7() {
        throw new RuntimeException("test7");
    }

    @Override
    public void test8() throws Exception {
        throw new Exception("test8");
    }

    @Override
    public void test9(String arg1) {
        throw new RuntimeException(arg1);
    }

    @Override
    public void test10(String arg1) throws Exception {
        throw new Exception(arg1);
    }

}
