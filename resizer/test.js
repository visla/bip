'use strict';

const ImageHelper = require('./lib/image-helper');
ImageHelper.resizeImage('/app/viktor.jpg', 'SQUARE_SMALL', (err, newPath) => {
    if (err) {
        console.log(error);
    } else {
        console.log('Success', newPath);
    }
});