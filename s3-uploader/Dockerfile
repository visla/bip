FROM maven
COPY target/s3uploader-0.0.1.jar /usr/src/app/
WORKDIR /usr/src/app

CMD ["java", "-jar", "/usr/src/app/s3uploader-0.0.1.jar"]