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
'use strict';

angular.module('Authentication')

    .factory('AuthenticationService',
        ['$http', '$cookieStore', '$rootScope', '$timeout', '$httpParamSerializerJQLike',
            function ($http, $cookieStore, $rootScope, $timeout, $httpParamSerializerJQLike) {
                var service = {};

                service.Login = function (clientId, username, password, callback) {

                    $http({
                        url: 'https://connect.voyages-sncf.com/auth/realms/VSC/protocol/openid-connect/token',
                        method: 'post',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        },
                        data: $httpParamSerializerJQLike({
                            client_id: clientId,
                            client_secret: '6db7358e-e488-4895-815f-708943d311c8',
                            username: username,
                            password: password,
                            grant_type: 'password'
                        })
                    })
                        .success(function (response) {
                            response.success = true;
                            callback(response);
                        })
                        .error(function () {
                            var response = {};
                            response.message = "Erreur d'autentification";
                            response.success = false;
                            callback(response);
                        });

                };

                service.SetCredentials = function (authentication) {
                    $rootScope.globals = {
                        currentUser: authentication
                    };
                    $http.defaults.headers.common['Authorization'] = 'Bearer ' + authentication.accessToken;
                    $http.defaults.headers.common['refresh_token'] = authentication.refreshToken;
                    $cookieStore.put('globals', $rootScope.globals);
                };

                service.ClearCredentials = function () {
                    $rootScope.globals = {};
                    $cookieStore.remove('globals');
                    $http.defaults.headers.common['Authorization'] = undefined;
                    $http.defaults.headers.common['refresh_token'] = undefined;
                };

                return service;
            }]);