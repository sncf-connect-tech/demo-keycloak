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
var config = require('./config')(process.env.NODE_ENV || 'dev');
var fs = require('fs');
var q = require('q');
var Request = require('request');
var bodyParser = require('body-parser');
var express = require("express");
var https = require('https');
var app = express();

var validator = require('express-validator');
app.use(validator());

app.set('view engine', 'pug');

// configure app to use bodyParser()
// this will let us get the data from a POST
app.use(bodyParser.urlencoded({extended: true}));
app.use(bodyParser.json());

// Accept every SSL certificate
process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

app.use(express.static('public'));

var options = {
    key: fs.readFileSync('../key.pem'),
    cert: fs.readFileSync('../cert.pem')
};

var buildUrl = function (protocol, endpoint, path) {
    return protocol + '://' + endpoint.url + ':' + endpoint.port + endpoint.path + path;
};

function generateToken(authorizationCode) {
    var deferred = q.defer();
    var client = config.client;
    Request({
        method: "POST",
        url: buildUrl('https', config.authentication, "/realms/VSC/protocol/openid-connect/token"),
        form: {
            client_id: client.id,
            client_secret: client.secret,
            code: authorizationCode,
            grant_type: "authorization_code",
            redirect_uri: "https://fr.voyages-sncf.com/signin"
        }
    }, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            if (response.headers['content-type'] === 'application/json') {
                deferred.resolve(JSON.parse(body));
            } else {
                deferred.resolve(body);
            }
        } else if (error) {
            deferred.reject(error);
        } else {
            deferred.reject(new Error(response.statusMessage, response.statusCode));
        }
    });

    return deferred.promise;
}

app.get('/signin', function (req, res) {

    var authorizationCode = req.query.code;

    var errors = [];

    generateToken(authorizationCode)
        .then(function (result) {
            console.info("authorization:" + result);

            var accessToken = result.access_token;
            var refreshToken = result.refresh_token;

            res.redirect(301, '/#/signin?accessToken=' + accessToken + '&refreshToken=' + refreshToken);
        })
        .fail(function (error) {
            errors.push(error);
            console.error(errors);

            res.status(500).send(errors);
        })
        .done();
});

https.createServer(options, app).listen(3020);

console.info("Running at Port 3020");