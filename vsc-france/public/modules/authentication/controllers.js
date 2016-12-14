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

    .controller('LoginController',
        ['$scope', '$rootScope', '$location', 'AuthenticationService',
            function ($scope, $rootScope, $location, AuthenticationService) {
                // reset login status
                AuthenticationService.ClearCredentials();
                $scope.clientId = 'vsc-france';

                $scope.login = function () {
                    $scope.dataLoading = true;
                    AuthenticationService.Login($scope.clientId, $scope.username, $scope.password, function (response) {
                        if (response.success) {
                            var authentication = {
                                accessToken: response.access_token,
                                refreshToken: response.refresh_token
                            };

                            AuthenticationService.SetCredentials(authentication);
                            $location.$$search = {};
                            $location.path('/');
                        } else {
                            $scope.error = response.message;
                            $scope.dataLoading = false;
                        }
                    });
                };
            }])
    .controller('SigninController',
        ['$scope', '$rootScope', '$location', '$routeParams', 'AuthenticationService',
            function ($scope, $rootScope, $location, $routeParams, AuthenticationService) {
                // reset login status
                AuthenticationService.ClearCredentials();

                var authentication = {
                    accessToken: $routeParams.accessToken,
                    refreshToken: $routeParams.refreshToken
                };

                AuthenticationService.SetCredentials(authentication);
                $location.path('/');
            }]);