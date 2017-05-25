docker network create bip

docker volume create --name=bip-upload-images

docker run -d \
    --net=bip \
    --restart=always \
    --name bip-resizer \
    --env-file "`pwd`"/envvars \
    -v bip-upload-images:/uploaded \
    visla/bip-resizer

docker run -d \
    --restart=always \
    --net=bip \
    --name bip-s3-uploader \
    -v bip-upload-images:/uploaded \
    -p 8090:80 \
    --env-file "`pwd`"/envvars \
    visla/bip-s3uploader:latest
