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
var config = {

    dev: {
        authentication: {
            url: 'connect.voyages-sncf.com',
            port: '443',
            path: '/auth'
        },
        client: {
            id: 'vsc-france',
            secret: '6db7358e-e488-4895-815f-708943d311c8'
        }
    }

};

module.exports = function (env) {
    return config[env];
};