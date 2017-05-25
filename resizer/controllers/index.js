'use strict';

const common = require('../common/');
const ImageHelper = require('../lib/image-helper');

class Controllers {
    setup(app) {
        this.app = app;

        app.get('/image/resize', this.handleImageResize.bind(this));
    }

    handleImageResize(req, res) {
        // TODO Validate image_size from query.
        const imagePath = req.query.filename;
        if (!imagePath) {
            return common.restRespondError(400, 'missing filename in query');
        }

        if (req.query.image_size === 'ORIGINAL') {
            common.restRespond(res, {
                resized_path: imagePath,
            });
            return;
        }

        ImageHelper.resizeImage(imagePath, req.query.image_size, (err, path) => {
            if (err) {
                return common.restRespondError(res, 500, 'error resizing');
            }

            common.restRespond(res, {
                resized_path: path,
            });
        });
    }
}

module.exports = new Controllers();