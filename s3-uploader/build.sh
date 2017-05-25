if [ -z "$1" ]
  then
    echo "No version supplied. Execute like ./build.sh 1.0.1 or similar"
    exit;
fi

docker build -t visla/bip-s3uploader:$1 .