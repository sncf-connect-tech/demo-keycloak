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
        ['$http', '$cookieStore', '$rootScope', '$timeout',
            function ($http, $cookieStore, $rootScope, $timeout) {
                var service = {};

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