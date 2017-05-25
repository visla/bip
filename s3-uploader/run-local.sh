docker volume create --name=bip-upload-images

docker run -d \
    --restart=always \
    --net=bip \
    --name bip-s3-uploader \
    -v bip-upload-images:/uploaded \
    -p 8090:80 \
    -w /usr/src/app \
    --env-file "`pwd`"/envvars \
    visla/bip-s3uploader:latest
