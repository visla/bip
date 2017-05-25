docker volume create --name=bip-upload-images

docker run -ti --rm --net=bip --name bip-resizer \
    --env-file "`pwd`"/envvars \
    -v bip-upload-images:/uploaded \
    -v "`pwd`":/app \
    -w /app \
    --env-file envvars \
    visla/node-imagemagick:6 /bin/bash
