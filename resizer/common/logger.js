'use strict';

var winston = require('winston');

var transports = [];
transports = [
    new (winston.transports.Console)({
        level: 'info',
        colorize: true,
        timestamp: true,
    }),
];

var logger = new (winston.Logger)({
    transports: transports
});

global.logger = logger;
module.exports = logger;