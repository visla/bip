docker volume create --name=bip-upload-images

docker run -ti --rm --net=bip --name bip-s3-uploader \
    --env-file "`pwd`"/envvars \
    -v "`pwd`":/usr/src/app \
    -v bip-upload-images:/uploaded \
    -w /usr/src/app \
    -p 8090:80 \
    maven /bin/bash
