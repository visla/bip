'use strict';

// make rootDir available.
global.rootDir = __dirname;
require('./common/logger');

const express = require('express');
const path = require('path');
const bodyParser = require('body-parser');
const app = express();

const controllers = require('./controllers');
controllers.setup(app);

app.listen(80, () => {
    logger.info('listening...');
});