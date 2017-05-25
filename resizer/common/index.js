'use strict';

/**
 * Respond with data.
 * @param  {[type]} res  [description]
 * @param  {[type]} data [description]
 * @return {[type]}      [description]
 */
exports.restRespond = function(res, data) {
    res.writeHead(200, {'Content-Type' : 'application/json'});
    res.end(JSON.stringify(data));
};

/**
 * Respond error.
 * @param  {[type]} res     [description]
 * @param  {[type]} code    [description]
 * @param  {[type]} message [description]
 * @return {[type]}         [description]
 */
exports.restRespondError = function(res, code, message) {
    var resultData = {
        message: message
    };

    res.writeHead(code, {'Content-Type' : 'application/json'});
    res.end(JSON.stringify(resultData));
};
