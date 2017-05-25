docker volume create --name=bip-upload-images

docker run -d --net=bip --name bip-resizer \
    --env-file "`pwd`"/envvars \
    -v bip-upload-images:/uploaded \
    -v "`pwd`":/app \
    -w /app \
    --env-file envvars \
    visla/bip-resizer
