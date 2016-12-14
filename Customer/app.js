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
var Keycloak = require('keycloak-connect');
var bodyParser = require('body-parser');

var https = require('https');
var express = require("express");
var app = express();

var validator = require('express-validator');
app.use(validator());

var customerDB = require('./DatabaseCustomer');

// configure app to use bodyParser()
// this will let us get the data from a POST
app.use(bodyParser.urlencoded({extended: true}));
app.use(bodyParser.json());

// Accept every SSL certificate
process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

var keycloak = new Keycloak({});
app.use(keycloak.middleware());

app.get('/customers/me', keycloak.protect(), function (req, res) {
    return res.send(customerDB.find());
});

var options = {
    key: fs.readFileSync('../key.pem'),
    cert: fs.readFileSync('../cert.pem')
};

https.createServer(options, app).listen(3010);

console.info("Running at Port 3010");