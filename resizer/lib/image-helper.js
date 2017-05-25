'use strict';

const gm = require('gm').subClass({imageMagick: true});
const path = require('path');

const IMAGE_SIZES = {
    'LARGE': {
        width: 1024,
        height: 768,
    },
    'SQUARE_MEDIUM': {
        width: 400,
        height: 400,
    },
    'SQUARE_SMALL': {
        width: 200,
        height: 200,
    }
};

class ImageHelper {
    static resizeImage(imageFilePath, imageSizeName = 'LARGE', callback) {
        var pathParts = path.parse(imageFilePath);
        var destinationPath = path.join(pathParts.dir, pathParts.name + '-' + imageSizeName + pathParts.ext);
        gm(imageFilePath)
            .resizeExact(IMAGE_SIZES[imageSizeName].width, IMAGE_SIZES[imageSizeName].height)
            .write(destinationPath, (err) => {
                if (err) {
                    logger.error('failed resizing image', {
                        imageFilePath: imageFilePath
                    });

                    return callback(err);
                }

                callback(null, destinationPath);
            });
    }
};

module.exports = ImageHelper;